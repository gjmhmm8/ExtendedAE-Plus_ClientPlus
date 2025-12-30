package com.fish.extendedae_plus_client;

import com.fish.extendedae_plus_client.config.EAEPCConfig;
import com.fish.extendedae_plus_client.integration.ContextModLoaded;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = ExtendedAEPlusClient.MODID, dist = Dist.CLIENT)
public class ExtendedAEPlusClient {
    public static final String MODID = "extendedae_plus_client";

    public ExtendedAEPlusClient(IEventBus eventBus, ModContainer modContainer) {
        EAEPCConfig.init(modContainer);
        ContextModLoaded.init();
    }

    public static ResourceLocation getLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @Mod(value = ExtendedAEPlusClient.MODID, dist = Dist.DEDICATED_SERVER)
    public static class ExtendedAEPlusServer {
        public ExtendedAEPlusServer(IEventBus eventBus, ModContainer modContainer) {
            LogUtils.getLogger()
                    .warn("This is a client-side mod and it won't work on servers. Please use ExtendedAE Plus (common) instead.");
        }
    }
}