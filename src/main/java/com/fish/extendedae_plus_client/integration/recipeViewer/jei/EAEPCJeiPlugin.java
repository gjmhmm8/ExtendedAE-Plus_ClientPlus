package com.fish.extendedae_plus_client.integration.recipeViewer.jei;

import com.fish.extendedae_plus_client.ExtendedAEPlusClient;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class EAEPCJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID =
            ExtendedAEPlusClient.getLocation("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        HelperJeiRuntime.setRuntime(jeiRuntime);
    }
}
