package com.fish.extendedae_plus_client.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将 {@link Component} 按指定语言（例如 en_us）转换为字符串。
 * <p>
 * 设计目标：只“接管” {@link TranslatableContents} 的翻译过程（语言无关），其余类型直接使用原生 getString/visit 行为。
 * 这用于在客户端不切换 UI 语言的前提下，复刻服务端常用语言（通常 en_us）生成的 displayName，从而与 S2C 列表字符串稳定对齐。
 */
public final class ComponentLocaleConverter {
    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    /**
     * cacheKey = locale -> (translationKey -> value)
     */
    private static final Map<String, Map<String, String>> LANG_CACHE = new HashMap<>();

    private ComponentLocaleConverter() {
    }

    public static String toLocaleString(Component component, String locale) {
        if (component == null) return "";
        if (locale == null || locale.isBlank()) return component.getString();

        var contents = component.getContents();
        if (contents instanceof TranslatableContents tc) {
            return renderHandledWithSiblings(component, renderTranslatable(tc, locale), locale);
        }
        if (contents instanceof PlainTextContents pt) {
            return renderHandledWithSiblings(component, pt.text(), locale);
        }

        // 其他 contents：遵循用户要求，直接使用原生 getString/visit 的结果（并由原生逻辑处理 siblings）
        return component.getString();
    }

    /**
     * MutableComponent 可能会在 siblings 中追加更多 Component（其中也可能包含 TranslatableContents）。
     * 对于我们“已接管”的 contents（Translatable/PlainText），这里手动拼接 siblings，确保嵌套翻译也按指定 locale 解析。
     */
    private static String renderHandledWithSiblings(Component component, String base, String locale) {
        StringBuilder out = new StringBuilder(base == null ? "" : base);
        try {
            var siblings = component.getSiblings();
            if (!siblings.isEmpty()) {
                for (Component s : siblings) {
                    out.append(toLocaleString(s, locale));
                }
            }
        } catch (Throwable ignored) {
            // 极端情况下接口/实现差异，直接返回 base
        }
        return out.toString();
    }

    public static String normalizeForCompare(String s) {
        if (s == null) return "";
        // 去除 MC 格式化符号 §x，并做基础 trim
        return s.replaceAll("§.", "").trim();
    }

    private static String renderTranslatable(TranslatableContents tc, String locale) {
        final String key = tc.getKey();
        final String fallback = tc.getFallback();
        String template = translateKey(key, fallback, locale);
        if (template == null) template = fallback != null ? fallback : key;

        Object[] args = tc.getArgs();
        if (args.length == 0) {
            return template;
        }

        // 复刻 TranslatableContents.decomposeTemplate：支持 %s / %1$s / %%，其余格式直接回退为原字符串
        StringBuilder out = new StringBuilder();
        Matcher matcher = FORMAT_PATTERN.matcher(template);
        int argAutoIndex = 0;
        int j = 0;
        while (matcher.find(j)) {
            int k = matcher.start();
            int l = matcher.end();
            if (k > j) {
                out.append(template, j, k);
            }

            String type = matcher.group(2);
            String raw = template.substring(k, l);
            if ("%".equals(type) && "%%".equals(raw)) {
                out.append('%');
            } else if (!"s".equals(type)) {
                // 不支持的格式：尽量不破坏输出
                out.append(raw);
            } else {
                String idxGroup = matcher.group(1);
                int idx = idxGroup != null ? Integer.parseInt(idxGroup) - 1 : argAutoIndex++;
                out.append(renderArg(args, idx, locale));
            }
            j = l;
        }
        if (j < template.length()) {
            out.append(template.substring(j));
        }
        return out.toString();
    }

    private static String renderArg(Object[] args, int index, String locale) {
        if (index < 0 || index >= args.length) return "";
        Object arg = args[index];
        if (arg == null) return "null";
        if (arg instanceof Component c) return toLocaleString(c, locale);
        return String.valueOf(arg);
    }

    private static String translateKey(String key, String fallback, String locale) {
        if (key == null || key.isEmpty()) return fallback;
        Map<String, String> map = LANG_CACHE.get(locale);
        if (map == null) {
            map = loadLangMap(locale);
            LANG_CACHE.put(locale, map);
        }
        return map.getOrDefault(key, fallback);
    }

    private static Map<String, String> loadLangMap(String locale) {
        Map<String, String> map = new HashMap<>();
        try {
            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            // 扫描所有命名空间下的 lang/<locale>.json 并合并键值（namespace 参数可能不准确，所以不使用它）
            Map<ResourceLocation, List<Resource>> resources = rm.listResourceStacks(
                    "lang",
                    rl -> rl != null && ("lang/" + locale + ".json").equals(rl.getPath())
            );

            for (var entry : resources.entrySet()) {
                List<Resource> resList = entry.getValue();
                for (var res : resList) {
                    try (Reader reader = openResourceAsReader(res)) {
                        if (reader == null) continue;
                        JsonReader jsonReader = new JsonReader(reader);
                        jsonReader.setLenient(true); // 兼容带注释/非严格 JSON 的语言文件
                        JsonObject obj = JsonParser.parseReader(jsonReader).getAsJsonObject();
                        for (var e : obj.entrySet()) {
                            if (e.getValue().isJsonPrimitive() && e.getValue().getAsJsonPrimitive().isString()) {
                                map.put(e.getKey(), e.getValue().getAsString());
                            }
                        }
                    } catch (Throwable exp) {
                        LogUtils.getLogger().error("EAEP CLC Err 1",exp);
                        // 单个资源失败不影响其他命名空间
                    }
                }
            }
        } catch (Throwable exp) {
            LogUtils.getLogger().error("EAEP CLC Err 2",exp);
            // 拿不到语言表就返回空 map，由调用方继续走原生 getString 兜底
        }
        return map;
    }

    /**
     * 兼容不同版本 Resource 的读取方式：优先 openAsReader()，否则 open() -> InputStreamReader。
     */
    private static Reader openResourceAsReader(Resource resource) {
        if (resource == null) return null;
        try{
            return resource.openAsReader();
        } catch (IOException e) {
            return null;
        }
    }
}


