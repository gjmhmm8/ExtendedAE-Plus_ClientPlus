package com.fish.extendedae_plus_client.integration.recipeViewer

import appeng.api.stacks.GenericStack
import com.mojang.datafixers.util.Pair

class ViewerEmpty : IRecipeViewer {
    override fun getHoveredStacks(mouseX: Double, mouseY: Double): MutableList<GenericStack?> {
        return mutableListOf()
    }

    override val hoveredStacks: MutableList<GenericStack?>
        get() = mutableListOf()

    override val favorites: MutableList<GenericStack?>
        get() = mutableListOf()

    override fun matchesKey(mouseKey: Int): Pair<Boolean, Boolean>? {
        return null
    }

    override fun addFavorite(stack: GenericStack) {
    }

    override fun setSearch(text: String) {
    }
}
