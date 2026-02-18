package com.fish.extendedae_plus_client.mixin.core.recipeViewer.jei;

import appeng.integration.modules.jei.transfer.EncodePatternTransferHandler;
import appeng.integration.modules.jeirei.EncodingHelper;
import com.fish.extendedae_plus_client.impl.EAEEncodingHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = EncodePatternTransferHandler.class, remap = false)
public class MixinEncodePatternTransfer {
    @Inject(method = "transferRecipe(Lnet/minecraft/world/inventory/AbstractContainerMenu;Ljava/lang/Object;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/world/entity/player/Player;ZZ)Lmezz/jei/api/recipe/transfer/IRecipeTransferError;", at = @At("HEAD"))
    private void onTransfer(AbstractContainerMenu menu,
                            Object recipeBase,
                            IRecipeSlotsView slotsView,
                            Player player,
                            boolean maxTransfer,
                            boolean doTransfer,
                            CallbackInfoReturnable<IRecipeTransferError> cir) {
        if (!doTransfer) return;
        if (ModList.get().isLoaded("emi")) return;
        EAEEncodingHelper.tiggerAutoEncoding();
        if (!(recipeBase instanceof Recipe<?> recipe)
                || EncodingHelper.isSupportedCraftingRecipe(recipe))
            return;

        EAEEncodingHelper.tryCollectKeywords(recipe);
        // Jei的这神必workstation真的能搞到吗?????
    }
}
