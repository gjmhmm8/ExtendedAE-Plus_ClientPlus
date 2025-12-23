package com.fish.extendedae_plus_client.impl.cache;

public class CacheCuttingKnife {
    public static boolean isHandlingBlockCopies() {
        return handlingBlockCopies;
    }

    public static void setHandlingBlockCopies(boolean handlingBlockCopies) {
        CacheCuttingKnife.handlingBlockCopies = handlingBlockCopies;
    }

    private static boolean handlingBlockCopies;
}
