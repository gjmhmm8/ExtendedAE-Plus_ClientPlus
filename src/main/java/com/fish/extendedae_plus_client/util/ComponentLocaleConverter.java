package com.fish.extendedae_plus_client.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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

public final class ComponentLocaleConverter {
    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
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
        return component.getString();
    }

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
        }
        return out.toString();
    }

    public static String normalizeForCompare(String s) {
        if (s == null) return "";
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
                        jsonReader.setLenient(true);
                        JsonObject obj = JsonParser.parseReader(jsonReader).getAsJsonObject();
                        for (var e : obj.entrySet()) {
                            if (e.getValue().isJsonPrimitive() && e.getValue().getAsJsonPrimitive().isString()) {
                                map.put(e.getKey(), e.getValue().getAsString());
                            }
                        }
                    } catch (Throwable exp) {
                        LogUtils.getLogger().error("EAEP CLC Err 1", exp);
                    }
                }
            }
        } catch (Throwable exp) {
            LogUtils.getLogger().error("EAEP CLC Err 2", exp);
        }
        return map;
    }

    private static Reader openResourceAsReader(Resource resource) {
        if (resource == null) return null;
        try {
            return resource.openAsReader();
        } catch (IOException e) {
            return null;
        }
    }
}

