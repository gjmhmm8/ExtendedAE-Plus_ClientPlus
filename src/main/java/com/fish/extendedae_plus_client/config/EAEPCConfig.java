package com.fish.extendedae_plus_client.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class EAEPCConfig {
    static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue autoPlateRepeat;
    public static final ModConfigSpec.EnumValue<ModeEncodingTransfer> modeEncodingTransfer;

    static {
        var builder = new ModConfigSpec.Builder();

        autoPlateRepeat = builder.defineInRange("autoPlateRepeat", 1, 1, 64);
        modeEncodingTransfer = builder.defineEnum("modeEncodingTransfer", ModeEncodingTransfer.MERGE_ADJACENCY);

        SPEC = builder.build();
    }

    public static void init(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, SPEC, "extendedae_plus/client.toml");

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
