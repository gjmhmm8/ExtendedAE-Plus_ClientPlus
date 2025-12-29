package com.fish.extendedae_plus_client.mixin.core.ae.menu;

import appeng.menu.AEBaseMenu;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.me.crafting.CraftingPlanSummary;
import com.fish.extendedae_plus_client.impl.ConstantCustomData;
import com.fish.extendedae_plus_client.impl.cache.CacheCrafting;
import net.minecraft.core.component.DataComponents;
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
    private CraftingPlanSummary plan;

    public MixinCraftingConfirm(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "startJob", at = @At("HEAD"))
    private void onJobStart(CallbackInfo ci) {
        if (isServerSide()) return;

        if (this.plan == null) return;
        if (this.plan.getEntries().stream().noneMatch(entry -> {
            var data = entry.getWhat().get(DataComponents.CUSTOM_DATA);
            if (data == null) return false;
            return data.contains(ConstantCustomData.autoCompletable.get());
        })) return;

        CacheCrafting.markPlan();
    }
}
