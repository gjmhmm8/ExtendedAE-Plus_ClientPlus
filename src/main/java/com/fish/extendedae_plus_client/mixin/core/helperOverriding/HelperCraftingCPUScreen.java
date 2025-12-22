package com.fish.extendedae_plus_client.mixin.core.helperOverriding;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.CraftingCPUScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.me.crafting.CraftingCPUMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CraftingCPUScreen.class, priority = 900)
public class HelperCraftingCPUScreen<TMenu extends CraftingCPUMenu> extends AEBaseScreen<TMenu> {
    public HelperCraftingCPUScreen(TMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }
}
