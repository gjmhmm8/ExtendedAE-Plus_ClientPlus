package com.fish.extendedae_plus_client.mixin.core.ae.menu;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.menu.AEBaseMenu;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.me.crafting.CraftingPlanSummary;
import com.fish.extendedae_plus_client.impl.ConstantCustomData;
import com.fish.extendedae_plus_client.impl.cache.CacheCrafting;
import com.fish.extendedae_plus_client.integration.recipeViewer.HelperRecipeViewer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftConfirmMenu.class, remap = false)
public abstract class MixinCraftingConfirm extends AEBaseMenu {
    @Shadow
    private CraftingPlanSummary plan;

    public MixinCraftingConfirm(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "startJob", at = @At("HEAD"))
    private void onJobStart(CallbackInfo ci) {
        if (this.isServerSide()) return;

        if (this.plan == null) return;
        if (this.plan.getEntries().stream().noneMatch(entry -> {
            var what = entry.getWhat();
            if (!(what instanceof AEItemKey itemKey)) return false;
            var tag = itemKey.getTag();
            return tag != null && tag.getBoolean(ConstantCustomData.autoCompletable.get());
        })) return;

        CacheCrafting.markPlan();
    }

    @Inject(method = "goBack", at = @At("HEAD"))
    private void onBack(CallbackInfo ci) {
        if (this.isServerSide()) return;
        if (!Screen.hasControlDown()) return;//TODO fix
        if (this.plan == null) return;

        this.plan.getEntries().forEach(entry -> {
            if (entry.getMissingAmount() <= 0) return;
            HelperRecipeViewer.addFavorite(
                    new GenericStack(entry.getWhat(), entry.getMissingAmount()));
        });
    }
}
