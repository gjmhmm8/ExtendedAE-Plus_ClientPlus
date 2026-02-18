package com.fish.extendedae_plus_client.integration.recipeViewer.emi

import appeng.api.stacks.GenericStack
import appeng.integration.modules.emi.EmiStackHelper
import com.fish.extendedae_plus_client.integration.recipeViewer.IRecipeViewer
import com.mojang.datafixers.util.Pair
import dev.emi.emi.api.EmiApi
import dev.emi.emi.config.EmiConfig
import dev.emi.emi.runtime.EmiFavorite
import dev.emi.emi.runtime.EmiFavorites

class ViewerEmi : IRecipeViewer {
    override fun getHoveredStacks(mouseX: Double, mouseY: Double): MutableList<GenericStack?> {
        return EmiApi.getHoveredStack(mouseX.toInt(), mouseY.toInt(), false).stack.emiStacks.stream()
            .map(EmiStackHelper::toGenericStack)
            .toList()
    }

    override val hoveredStacks: MutableList<GenericStack?>
        get() = EmiApi.getHoveredStack(false).stack.emiStacks.stream()
            .map(EmiStackHelper::toGenericStack)
            .toList()

    override val favorites: MutableList<GenericStack?>
        get() = EmiFavorites.favorites.stream()
            .map(EmiFavorite::getEmiStacks)
            .map { list -> list[0] }
            .map(EmiStackHelper::toGenericStack)
            .toList()

    override fun matchesKey(mouseKey: Int): Pair<Boolean, Boolean>? {
        return if (EmiConfig.cheatOneToCursor.matchesMouse(mouseKey)) Pair<Boolean, Boolean>(false, false)
        else if (EmiConfig.cheatOneToInventory.matchesMouse(mouseKey)) Pair<Boolean, Boolean>(false, true)
        else if (EmiConfig.cheatStackToCursor.matchesMouse(mouseKey)) Pair<Boolean, Boolean>(true, false)
        else if (EmiConfig.cheatStackToInventory.matchesMouse(mouseKey)) Pair<Boolean, Boolean>(true, true)
        else null
    }

    override val isCheatMode: Boolean
        get() = EmiApi.isCheatMode()

    override fun addFavorite(stack: GenericStack) {
        EmiFavorites.addFavorite(EmiStackHelper.toEmiStack(stack))
    }

    override fun setSearch(text: String) {
        EmiApi.setSearchText(text)
    }
}
