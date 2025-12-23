package com.fish.extendedae_plus_client;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class EAEPCConfig {
    static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue autoPlateRepeat;

    static {
        var builder = new ModConfigSpec.Builder();

        autoPlateRepeat = builder.defineInRange("autoPlateRepeat", 1, 1, 64);

        SPEC = builder.build();
    }

    static void init(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, SPEC, "extendedae_plus/client.toml");
    }
}
