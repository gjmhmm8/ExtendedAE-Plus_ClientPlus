package com.fish.extendedae_plus_client.impl.cache;

import appeng.api.crafting.IPatternDetails;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class CacheProvider {
    private static final Map<Integer, PatternContainerRecord> listProvider = new HashMap<>();
    private static final Map<IPatternDetails, Integer> selectedProvider = new HashMap<>();

    public static void markPattern(IPatternDetails pattern, int hashGroup) {
        selectedProvider.put(pattern, hashGroup);
    }

    public static void clearPattern() {
        selectedProvider.clear();
    }

    public static @Nullable Integer findProvider(@Nullable IPatternDetails pattern) {
        return selectedProvider.get(pattern);
    }

    public static void putProvider(PatternContainerRecord record) {
        listProvider.put(record.getGroup().hashCode(), record);
    }

    public static void clearProvider() {
        listProvider.clear();
    }

    public static Map<Integer, PatternContainerRecord> getProviderList() {
        return listProvider;
    }
}
