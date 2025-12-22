package com.fish.extendedae_plus_client.mixin.core.helperOverriding;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.PatternAccessTermMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PatternAccessTermScreen.class, priority = 900)
public class HelperAccessScreen<TMenu extends PatternAccessTermMenu> extends AEBaseScreen<TMenu> {
    public HelperAccessScreen(TMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }
}
