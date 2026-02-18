package com.fish.extendedae_plus_client.mixin.core.recipeViewer;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import com.fish.extendedae_plus_client.impl.ConstantCustomData;
import com.fish.extendedae_plus_client.integration.recipeViewer.HelperRecipeViewer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = EncodingHelper.class, remap = false)
public class MixinEncodingHelper {
    // 客户端：注入优先使用JEI书签的物品，流体
    @Inject(method = "getIngredientPriorities", at = @At("TAIL"), cancellable = true, remap = false)
    private static void epp$addJeiIngredientPriorities(MEStorageMenu menu, Comparator<GridInventoryEntry> comparator, CallbackInfoReturnable<Map<AEKey, Integer>> cir) {
        var result = cir.getReturnValue();
        var index = new AtomicInteger(Integer.MAX_VALUE);

        var player = Minecraft.getInstance().player;
        if (player != null) player.getInventory().items.stream()
                .filter(PatternDetailsHelper::isEncodedPattern)
                .map(pattern -> PatternDetailsHelper.decodePattern(pattern, Minecraft.getInstance().level))
                .filter(Objects::nonNull)
                .map(IPatternDetails::getPrimaryOutput)
                .filter(stack -> {
                    var what = stack.what();
                    if (!(what instanceof AEItemKey itemKey)) return true;
                    var tag = itemKey.getTag();
                    return tag == null || !tag.getBoolean(ConstantCustomData.autoCompletable.get());
                }).forEach(stack -> result.put(stack.what(), index.getAndDecrement()));

        HelperRecipeViewer.getFavorites().forEach(favorite -> {
            if (favorite != null) result.put(favorite.what(), index.getAndDecrement());
        });

        cir.setReturnValue(result);
    }
}
