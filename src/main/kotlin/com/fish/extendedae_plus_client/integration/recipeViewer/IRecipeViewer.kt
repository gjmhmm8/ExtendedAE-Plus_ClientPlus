package com.fish.extendedae_plus_client.integration.recipeViewer

import appeng.api.stacks.GenericStack
import com.mojang.datafixers.util.Pair

interface IRecipeViewer {
    fun getHoveredStacks(mouseX: Double, mouseY: Double): MutableList<GenericStack?>

    val hoveredStacks: MutableList<GenericStack?>

    val favorites: MutableList<GenericStack?>

    fun matchesKey(mouseKey: Int): Pair<Boolean, Boolean>?

    val isCheatMode: Boolean
        get() = true

    fun addFavorite(stack: GenericStack)

    fun setSearch(text: String)
}
