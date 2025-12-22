package com.fish.extendedae_plus_client.impl;

import appeng.menu.me.crafting.CraftingStatusEntry;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CacheCrafting {
    private final static Set<Component> markedPlan = new HashSet<>();
    private static boolean opening;

    public static void markPlan(Component cpuName) {
        markedPlan.add(cpuName);
    }

    public static boolean planMatches(List<CraftingStatusEntry> syncedPlan, Component cpuName) {
        if (!markedPlan.contains(cpuName)) return false;

        if (syncedPlan.isEmpty()) return false;

        boolean matches = true;
        for (var entry : syncedPlan) {
            matches &= entry.getPendingAmount() == 0;
        }
        if (matches) markedPlan.remove(cpuName);
        return matches;
    }

    public static void remove(Component cpuName) {
        markedPlan.remove(cpuName);
    }

    public static boolean isEmpty() {
        return markedPlan.isEmpty();
    }

    public static boolean isOpening() {
        if (opening) {
            opening = false;
            return true;
        } else return false;
    }

    public static void setOpening(boolean opening) {
        CacheCrafting.opening = opening;
    }
}
