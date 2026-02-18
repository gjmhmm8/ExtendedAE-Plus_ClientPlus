package com.fish.extendedae_plus_client.integration.recipeViewer.jei

import com.fish.extendedae_plus_client.ExtendedAEPlusClient
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class EAEPCJeiPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation {
        return UID
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        HelperJeiRuntime.setRuntime(jeiRuntime)
    }

    companion object {
        private val UID = ExtendedAEPlusClient.getLocation("jei_plugin")
    }
}
