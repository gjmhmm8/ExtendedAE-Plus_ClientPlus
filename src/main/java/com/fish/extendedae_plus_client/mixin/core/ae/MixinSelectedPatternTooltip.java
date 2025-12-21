package com.fish.extendedae_plus_client.mixin.core.ae;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.EncodedPatternItem;
import com.fish.extendedae_plus_client.impl.CacheProvider;
import com.fish.extendedae_plus_client.util.UtilKeyBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EncodedPatternItem.class)
public class MixinSelectedPatternTooltip {
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void onTooltip(ItemStack stack,
                           Item.TooltipContext context,
                           List<Component> tooltipComponents,
                           TooltipFlag tooltipFlag,
                           CallbackInfo ci) {
        var hashGroup = CacheProvider.findProvider(
                PatternDetailsHelper.decodePattern(stack, Minecraft.getInstance().level));
        var record = CacheProvider.getProviderList().get(hashGroup);
        if (record == null) return;

        tooltipComponents.add(UtilKeyBuilder.of(UtilKeyBuilder.tooltip)
                .item(AEItems.PROCESSING_PATTERN)
                .addStr("selected_provider")
                .args(record.getGroup().name().getString())
                .build());
    }
}
