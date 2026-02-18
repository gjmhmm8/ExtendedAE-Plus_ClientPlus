package com.fish.extendedae_plus_client.mixin.impl.helper;

/**
 * ae2wtlib API changed significantly between upstream versions.
 * Keep this helper as a safe no-op on Forge 1.20.1 until full API remap is finished.
 */
public class WTLibHelper {
    public static final Object PATTERN_ACCESS = new Object();
    public static final Object EX_PATTERN_ACCESS = new Object();
    public static final Object PATTERN_ENCODING = new Object();

    private WTLibHelper() {
    }

    public static boolean openTerminalCyc(Object definition) {
        return false;
    }

    public static void openTerminalHK(Object terminal) {
    }

    public static void goBackCyc() {
    }
}
