package com.fish.extendedae_plus_client.mixin.core.ae.screen.widgets;

import appeng.menu.slot.FakeSlot;
import com.fish.extendedae_plus_client.impl.AliasGetter;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FakeSlot.class)
public class MixinFakeSlot {
    @Inject(method = "setFilterTo", at = @At("HEAD"))
    private void onFilterSetting(ItemStack itemStack, CallbackInfo ci) {
        AliasGetter.recipeKeywords.clear();
    }
}
