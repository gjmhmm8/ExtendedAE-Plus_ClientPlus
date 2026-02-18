package com.fish.extendedae_plus_client.mixin.core.helperOverriding;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import com.glodblock.github.extendedae.container.ContainerExPatternTerminal;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GuiExPatternTerminal.class, priority = 900, remap = false)
public abstract class HelperExAccessScreen<TMenu extends ContainerExPatternTerminal> extends AEBaseScreen<TMenu> {
    public HelperExAccessScreen(TMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
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
