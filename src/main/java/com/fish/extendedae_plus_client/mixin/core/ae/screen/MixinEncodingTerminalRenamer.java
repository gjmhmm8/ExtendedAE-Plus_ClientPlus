package com.fish.extendedae_plus_client.mixin.core.ae.screen;

import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.fish.extendedae_plus_client.screen.ScreenStacksReproperties;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatternEncodingTermScreen.class)
public class MixinEncodingTerminalRenamer<TMenu extends PatternEncodingTermMenu>
        extends MEStorageScreen<TMenu> {
    public MixinEncodingTerminalRenamer(TMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClick(double xCoord, double yCoord, int btn, CallbackInfoReturnable<Boolean> cir) {
        if (this.minecraft == null) return;

        if (!this.menu.canModifyAmountForSlot(this.hoveredSlot)) return;

        if (!this.minecraft.options.keyPickItem.matchesMouse(btn)) return;
        if (!Screen.hasControlDown()) return;

        var stack = this.hoveredSlot.getItem();
        var screen = new ScreenStacksReproperties<>(
                (PatternEncodingTermScreen<TMenu>)(Object) this,
                stack,
                newStack -> {
                    var message = new InventoryActionPacket(
                            InventoryAction.SET_FILTER, this.hoveredSlot.index, newStack);
                    PacketDistributor.sendToServer(message);
                },
                this.hoveredSlot == this.menu.getProcessingOutputSlots()[0]
        );
        this.switchToScreen(screen);
        cir.setReturnValue(true);
    }
}
