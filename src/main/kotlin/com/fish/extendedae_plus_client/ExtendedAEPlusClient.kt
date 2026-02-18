package com.fish.extendedae_plus_client

import com.fish.extendedae_plus_client.config.EAEPCConfig
import com.fish.extendedae_plus_client.integration.ContextModLoaded
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.common.Mod


@Mod(ExtendedAEPlusClient.MODID)
class ExtendedAEPlusClient {
    init {
        EAEPCConfig.init()
        ContextModLoaded.init()
    }


    companion object {
        const val MODID = "extendedae_plus_client"

        internal fun getLocation(path : String) : ResourceLocation =
            ResourceLocation(MODID, path)
    }
}