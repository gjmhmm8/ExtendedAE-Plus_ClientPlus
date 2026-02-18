package com.fish.extendedae_plus_client.mixin.core.recipeViewer.emi;

import appeng.integration.modules.emi.EmiEncodePatternHandler;
import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.AEBaseMenu;
import com.fish.extendedae_plus_client.impl.EAEEncodingHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EmiEncodePatternHandler.class, remap = false)
public abstract class MixinEncodePatternTransfer {
    @Inject(method = "transferRecipe(Lappeng/menu/AEBaseMenu;Lnet/minecraft/world/item/crafting/Recipe;Ldev/emi/emi/api/recipe/EmiRecipe;Z)Lappeng/integration/modules/emi/AbstractRecipeHandler$Result;",
            at = @At("HEAD"), require = 0)
    private void onTransfer(AEBaseMenu menu, Recipe<?> recipe,
                                   EmiRecipe emiRecipe, boolean doTransfer, CallbackInfoReturnable<?> cir) {
        if (!doTransfer) return;
        EAEEncodingHelper.tiggerAutoEncoding();
        if (recipe != null && EncodingHelper.isSupportedCraftingRecipe(recipe)) return;
        EAEEncodingHelper.tryCollectKeywords(emiRecipe);
    }
}
