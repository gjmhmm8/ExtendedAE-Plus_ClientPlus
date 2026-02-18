package com.fish.extendedae_plus_client.mixin.core.ae.registryObject;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.EncodedPatternItem;
import com.fish.extendedae_plus_client.impl.ConstantCustomData;
import com.fish.extendedae_plus_client.impl.cache.CacheProvider;
import com.fish.extendedae_plus_client.util.UtilKeyBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = EncodedPatternItem.class, remap = false)
public class MixinSelectedPatternTooltip {
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void onTooltip(ItemStack stack,
                           Level level,
                           List<Component> tooltipComponents,
                           TooltipFlag tooltipFlag,
                           CallbackInfo ci) {
        var group = CacheProvider.findProvider(
                PatternDetailsHelper.decodePattern(stack, Minecraft.getInstance().level));
        if(group==null)return;
        tooltipComponents.add(UtilKeyBuilder.of(UtilKeyBuilder.tooltip)
                    .item(AEItems.PROCESSING_PATTERN)
                    .addStr("selected_provider")
                    .args(group.name().getString())
                    .build());


        var pattern = PatternDetailsHelper.decodePattern(stack, Minecraft.getInstance().level);
        if (pattern == null) return;
        var what = pattern.getPrimaryOutput().what();
        if (!(what instanceof AEItemKey itemKey)) return;
        var tag = itemKey.getTag();
        if (tag == null || !tag.getBoolean(ConstantCustomData.autoCompletable.get())) return;
        tooltipComponents.add(UtilKeyBuilder.of(UtilKeyBuilder.tooltip)
                .item(AEItems.PROCESSING_PATTERN)
                .addStr("auto_completable")
                .build());
    }
}
