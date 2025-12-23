package com.fish.extendedae_plus_client.mixin.core.ae.screen;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.CraftingCPUScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.network.serverbound.GuiActionPacket;
import appeng.core.network.serverbound.SwitchGuisPacket;
import appeng.menu.me.crafting.CraftingCPUMenu;
import appeng.menu.me.crafting.CraftingStatus;
import com.fish.extendedae_plus_client.impl.CacheCrafting;
import com.fish.extendedae_plus_client.impl.ConstantCustomData;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperButtonOnPressModifier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingCPUScreen.class)
public class MixinCraftingCPU<TMenu extends CraftingCPUMenu> extends AEBaseScreen<TMenu> {
    @Shadow
    @Final
    private Button cancel;
    @Shadow
    private CraftingStatus status;

    public MixinCraftingCPU(TMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onCanceling(CallbackInfo ci) {
        if (this.cancel instanceof HelperButtonOnPressModifier helper)
            helper.eaep$setOnPress(this::eaep$cancelCrafting);
    }

    @Unique
    private void eaep$cancelCrafting(Button button) {
        this.getMenu().cancelCrafting();
        for (var entry : this.status.getEntries()) {
            if (entry.getWhat() == null) continue;

            var data = entry.getWhat().get(DataComponents.CUSTOM_DATA);
            if (data == null) continue;

            if (!data.contains(ConstantCustomData.autoCompletable.get()))
                continue;

            CacheCrafting.cancelPlan();
            break;
        }
    }

    @Inject(method = "postUpdate", at = @At("TAIL"))
    private void onUpdate(CraftingStatus status, CallbackInfo ci) {
        boolean matched = false;
        for (var entry : this.status.getEntries()) {
            if (entry.getWhat() == null) continue;

            var data = entry.getWhat().get(DataComponents.CUSTOM_DATA);
            if (data == null) continue;

            if (!data.contains(ConstantCustomData.autoCompletable.get()))
                continue;
            matched = entry.getPendingAmount() == 0;
            break;
        }
        if (matched) {
            var packetCancelCrafting = new GuiActionPacket(
                    this.menu.containerId, "cancelCrafting", null);
            PacketDistributor.sendToServer(packetCancelCrafting);
            CacheCrafting.cancelPlan();
        }

        if (!CacheCrafting.isOpening()) return;
        PacketDistributor.sendToServer(SwitchGuisPacket.returnToParentMenu());
    }
}
