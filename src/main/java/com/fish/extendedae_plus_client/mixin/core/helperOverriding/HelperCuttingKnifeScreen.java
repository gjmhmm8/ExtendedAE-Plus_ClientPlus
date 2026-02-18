package com.fish.extendedae_plus_client.mixin.core.helperOverriding;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.QuartzKnifeScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.QuartzKnifeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = QuartzKnifeScreen.class, priority = 900, remap = false)
public abstract class HelperCuttingKnifeScreen extends AEBaseScreen<QuartzKnifeMenu> {
    public HelperCuttingKnifeScreen(QuartzKnifeMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }
}
