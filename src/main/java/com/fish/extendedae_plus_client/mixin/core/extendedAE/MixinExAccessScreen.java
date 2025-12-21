package com.fish.extendedae_plus_client.mixin.core.extendedAE;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.client.gui.style.ScreenStyle;
import com.fish.extendedae_plus_client.impl.CacheProvider;
import com.fish.extendedae_plus_client.mixin.impl.HelperPatternMoving;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import com.glodblock.github.extendedae.container.ContainerExPatternTerminal;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(GuiExPatternTerminal.class)
public class MixinExAccessScreen<TMenu extends ContainerExPatternTerminal> extends AEBaseScreen<TMenu> {
    @Shadow
    @Final
    private HashMap<Long, PatternContainerRecord> byId;

    @Unique
    private HelperPatternMoving eaep$helperMoving;

    public MixinExAccessScreen(TMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        CacheProvider.clearProvider();
        this.eaep$helperMoving = new HelperPatternMoving(this);
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        this.eaep$helperMoving.clearReservation();
        if (!Screen.hasShiftDown())
            CacheProvider.clearPattern();
    }

    @Inject(method = "postFullUpdate", at = @At("TAIL"))
    private void onProviderListSync(long inventoryId,
                                    long sortBy,
                                    PatternContainerGroup group,
                                    int inventorySize,
                                    Int2ObjectMap<ItemStack> slots,
                                    CallbackInfo ci) {
        CacheProvider.putProvider(this.byId.get(inventoryId));
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"))
    private void onUpdating(CallbackInfo ci) {
        this.eaep$helperMoving.movePattern();
    }
}
