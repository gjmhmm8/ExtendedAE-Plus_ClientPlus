package com.fish.extendedae_plus_client.mixin.core.ae.screen;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.implementations.PatternAccessTermMenu;
import com.fish.extendedae_plus_client.impl.cache.CacheProvider;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperPatternMoving;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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

@Mixin(PatternAccessTermScreen.class)
public class MixinAccessTerminal<TMenu extends PatternAccessTermMenu> extends AEBaseScreen<TMenu> {
    @Shadow
    @Final
    private HashMap<Long, PatternContainerRecord> byId;
    @Shadow
    @Final
    private AETextField searchField;

    @Unique
    private HelperPatternMoving eaep$helperMoving;

    public MixinAccessTerminal(TMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        CacheProvider.clearProvider();
        CacheProvider.clearPatternAlready();
        this.eaep$helperMoving = new HelperPatternMoving(this);
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        this.eaep$helperMoving.clearReservation();
    }

    @Inject(method = "postFullUpdate", at = @At("TAIL"))
    private void onProviderListSync(long inventoryId,
                                    long sortBy,
                                    PatternContainerGroup group,
                                    int inventorySize,
                                    Int2ObjectMap<ItemStack> slots,
                                    CallbackInfo ci) {
        CacheProvider.putProvider(this.byId.get(inventoryId),inventorySize-slots.size()>0);
        for(var i:slots.values()){
            var patternDetail=PatternDetailsHelper.decodePattern(i, this.getPlayer().level());
            if (patternDetail != null) {
                CacheProvider.markPatternAlready(patternDetail);
            }
        }
    }

    @Inject(method = "postIncrementalUpdate", at = @At("HEAD"))
    private void onProviderListSyncInc(long inventoryId, Int2ObjectMap<ItemStack> slots, CallbackInfo ci) {
        for(var i:slots.int2ObjectEntrySet()){
            if(i.getValue()==ItemStack.EMPTY){
                CacheProvider.putProvider(this.byId.get(inventoryId),true);
                var pattern=this.byId.get(inventoryId).getInventory().getStackInSlot(i.getIntKey());
                var patternDetail=PatternDetailsHelper.decodePattern(pattern, this.getPlayer().level());
                if (patternDetail != null) {
                    CacheProvider.unmarkPatternAlready(patternDetail);
                }
            }
            var patternDetail=PatternDetailsHelper.decodePattern(i.getValue(), this.getPlayer().level());
            if (patternDetail != null) {
                CacheProvider.markPatternAlready(patternDetail);
            }
        }
    }

    @Inject(method = "updateBeforeRender", at = @At("HEAD"))
    private void onRenderUpdating(CallbackInfo ci) {
        if (this.eaep$helperMoving.isEmpty()) return;
        this.searchField.setFocused(false);
    }

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void onUpdating(CallbackInfo ci) {
        this.eaep$helperMoving.movePattern();
    }
}
