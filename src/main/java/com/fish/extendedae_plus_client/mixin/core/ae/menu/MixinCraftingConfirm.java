package com.fish.extendedae_plus_client.mixin.core.ae.menu;

import appeng.menu.AEBaseMenu;
import appeng.menu.me.crafting.CraftConfirmMenu;
import com.fish.extendedae_plus_client.impl.CacheCrafting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftConfirmMenu.class)
public class MixinCraftingConfirm extends AEBaseMenu {
    @Shadow
    public Component cpuName;

    public MixinCraftingConfirm(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "startJob", at = @At("HEAD"))
    private void onJobStart(CallbackInfo ci) {
        if (isServerSide()) return;
        if (!Screen.hasControlDown()) return;
        CacheCrafting.markPlan(this.cpuName);
    }
}
