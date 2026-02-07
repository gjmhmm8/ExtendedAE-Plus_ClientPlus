package com.fish.extendedae_plus_client.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将 {@link Component} 按指定语言（例如 en_us）转换为字符串。
 * <p>
 * 设计目标：只“接管” {@link TranslatableContents} 的翻译过程（语言无关），其余类型直接使用原生 getString/visit 行为。
 * 这用于在客户端不切换 UI 语言的前提下，复刻服务端常用语言（通常 en_us）生成的 displayName，从而与 S2C 列表字符串稳定对齐。
 */
public final class ComponentLocaleConverter {
    private ComponentLocaleConverter() {}

    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    /** cacheKey = namespace|locale -> (translationKey -> value) */
    private static final Map<String, Map<String, String>> LANG_CACHE = new HashMap<>();

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
             if (siblings != null && !siblings.isEmpty()) {
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
        String namespace = guessNamespaceFromTranslationKey(key);
        String cacheKey = namespace + "|" + locale;
        Map<String, String> map = LANG_CACHE.get(cacheKey);
        if (map == null) {
            map = loadLangMap(namespace, locale);
            LANG_CACHE.put(cacheKey, map);
        }
        return map.getOrDefault(key, fallback);
    }

    private static String guessNamespaceFromTranslationKey(String key) {
        // 经验规则：绝大多数 translation key 形如 block.<namespace>.<path> / item.<namespace>.<path> / gui.<namespace>....
        // 若无法解析，则回退 minecraft
        String[] parts = key.split("\\.");
        if (parts.length >= 2 && !parts[1].isEmpty()) return parts[1];
        return "minecraft";
    }

    private static Map<String, String> loadLangMap(String namespace, String locale) {
        Map<String, String> map = new HashMap<>();
        try {
            Object rm = Minecraft.getInstance().getResourceManager();
            Object res = tryGetResource(rm, ResourceLocation.fromNamespaceAndPath(namespace, "lang/" + locale + ".json"));
            if (res == null) return map;

            try (Reader reader = openResourceAsReader(res)) {
                if (reader == null) return map;
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                for (var e : obj.entrySet()) {
                    if (e.getValue().isJsonPrimitive()) {
                        map.put(e.getKey(), e.getValue().getAsString());
                    }
                }
            }
        } catch (Throwable ignored) {
            // 拿不到语言表就返回空 map，由调用方继续走原生 getString 兜底
        }
        return map;
    }

    /**
     * 兼容不同版本 ResourceManager#getResource 签名：
     * - Optional<Resource>
     * - Resource
     */
    private static Object tryGetResource(Object resourceManager, ResourceLocation rl) throws Exception {
        if (resourceManager == null) return null;
        Method m = resourceManager.getClass().getMethod("getResource", ResourceLocation.class);
        Object ret = m.invoke(resourceManager, rl);
        if (ret instanceof Optional<?> opt) {
            return opt.orElse(null);
        }
        return ret;
    }

    /**
     * 兼容不同版本 Resource 的读取方式：优先 openAsReader()，否则 open() -> InputStreamReader。
     */
    private static Reader openResourceAsReader(Object resource) {
        if (resource == null) return null;
        try {
            Method openAsReader = resource.getClass().getMethod("openAsReader");
            Object r = openAsReader.invoke(resource);
            if (r instanceof Reader reader) return reader;
        } catch (Throwable ignored) {
        }
        try {
            Method open = resource.getClass().getMethod("open");
            Object is = open.invoke(resource);
            if (is instanceof InputStream in) {
                return new InputStreamReader(in, StandardCharsets.UTF_8);
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}


