package com.fish.extendedae_plus_client.mixin.core.recipeViewer.emi;

import appeng.integration.modules.emi.EmiEncodePatternHandler;
import appeng.integration.modules.itemlists.EncodingHelper;
import appeng.menu.AEBaseMenu;
import com.fish.extendedae_plus_client.impl.AliasGetter;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmiEncodePatternHandler.class)
public abstract class MixinEncodePatternTransfer {
    @Inject(method = "transferRecipe(Lappeng/menu/AEBaseMenu;Lnet/minecraft/world/item/crafting/RecipeHolder;Ldev/emi/emi/api/recipe/EmiRecipe;Z)Lappeng/integration/modules/emi/AbstractRecipeHandler$Result;",
            at = @At("HEAD"), require = 0)
    private static void onTransfer(AEBaseMenu menu, RecipeHolder<?> holder,
                                   EmiRecipe emiRecipe, boolean doTransfer, CallbackInfoReturnable<?> cir) {
        if (!doTransfer) return;
        if (holder != null && EncodingHelper.isSupportedCraftingRecipe(holder.value())) return;

        AliasGetter.tryCollectKeywords(emiRecipe);
    }
}
