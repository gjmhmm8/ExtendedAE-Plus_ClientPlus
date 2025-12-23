package com.fish.extendedae_plus_client.impl.cache;

public final class CacheCrafting {
    private static int markedPlan = 0;
    private static boolean opening;

    public static void markPlan() {
        markedPlan++;
    }

    public static void cancelPlan() {
        markedPlan--;
    }

    public static boolean isEmpty() {
        return markedPlan == 0;
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
