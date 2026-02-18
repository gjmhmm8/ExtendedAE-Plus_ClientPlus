package com.fish.extendedae_plus_client.integration.recipeViewer.jei

import appeng.api.stacks.AEFluidKey
import appeng.api.stacks.AEItemKey
import appeng.api.stacks.GenericStack
import appeng.integration.modules.jei.GenericEntryStackHelper
import com.fish.extendedae_plus_client.integration.ContextModLoaded
import com.fish.extendedae_plus_client.integration.recipeViewer.IRecipeViewer
import com.mojang.datafixers.util.Pair
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.forge.ForgeTypes
import mezz.jei.api.ingredients.IIngredientType
import net.minecraft.client.gui.screens.Screen
import org.lwjgl.glfw.GLFW

class ViewerJei : IRecipeViewer {
    override fun getHoveredStacks(mouseX: Double, mouseY: Double): MutableList<GenericStack?> {
        return hoveredStacks
    }

    override val hoveredStacks: MutableList<GenericStack?>
        get() {
            val hovered = HelperJeiRuntime.ingredientUnderMouse.orElse(null)
            return if (hovered != null) mutableListOf(GenericEntryStackHelper.ingredientToStack(hovered))
            else mutableListOf()
        }

    override val favorites: MutableList<GenericStack?>
        get() = HelperJeiRuntime.bookmarkList.stream()
            .map { ingredient -> GenericEntryStackHelper.ingredientToStack(ingredient) }
            .toList()

    override fun matchesKey(mouseKey: Int): Pair<Boolean, Boolean>? {
        var match = false
        var stack = false
        var toInv = true

        if (Screen.hasControlDown()) match = true
        if (match && Screen.hasShiftDown()) stack = true

        if (mouseKey == GLFW.GLFW_MOUSE_BUTTON_RIGHT) toInv = false

        return if (match)
            Pair<Boolean, Boolean>(stack, toInv)
        else
            null
    }

    override val isCheatMode: Boolean
        get() = HelperJeiRuntime.jeiCheatModeEnabled

    override fun addFavorite(stack: GenericStack) {
        val key = stack.what()
        if (key is AEItemKey) HelperJeiRuntime.addFavorite(key.toStack(), VanillaTypes.ITEM_STACK)
        else if (key is AEFluidKey) HelperJeiRuntime.addFavorite(
            key.toStack(1000),
            ForgeTypes.FLUID_STACK
        )
        else if (ContextModLoaded.mekanism.isLoaded && ContextModLoaded.appliedMekanistics.isLoaded) {
            try {
                val clazzTypeKey = Class.forName("me.ramidzkh.mekae2.ae2.MekanismKey")
                if (!clazzTypeKey.isAssignableFrom(key.javaClass)) return

                val fieldStack = clazzTypeKey.getMethod("getStack").invoke(key)

                val fieldTypeIngredient = Class.forName("mekanism.client.recipe_viewer.jei.MekanismJEI")
                    .getField("TYPE_CHEMICAL").get(null) as IIngredientType<Any>

                HelperJeiRuntime.addFavorite(fieldStack, fieldTypeIngredient)
            } catch (_: Exception) {
            }
        }
    }

    override fun setSearch(text: String) {
        HelperJeiRuntime.setIngredientFilterText(text)
    }
}
