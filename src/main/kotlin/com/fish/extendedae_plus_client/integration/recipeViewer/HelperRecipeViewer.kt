package com.fish.extendedae_plus_client.integration.recipeViewer

import appeng.api.stacks.GenericStack
import com.fish.extendedae_plus_client.integration.ContextModLoaded
import com.fish.extendedae_plus_client.integration.recipeViewer.emi.ViewerEmi
import com.fish.extendedae_plus_client.integration.recipeViewer.jei.ViewerJei
import com.mojang.datafixers.util.Pair
import java.util.*

object HelperRecipeViewer {
    private var activeViewer: IRecipeViewer? = null

    fun init() {
        activeViewer = if (ContextModLoaded.emi.isLoaded) ViewerEmi()
        else if (ContextModLoaded.jei.isLoaded) ViewerJei()
        else ViewerEmpty()
    }

    val viewer: Optional<IRecipeViewer>
        get() {
            if (activeViewer == null) init()
            return Optional.ofNullable<IRecipeViewer>(activeViewer)
        }

    @JvmStatic
    val hoveredStacks: MutableList<GenericStack?>
        get() = viewer
            .map(IRecipeViewer::hoveredStacks)
            .orElse(mutableListOf())

    @JvmStatic
    val favorites: MutableList<GenericStack?>
        get() = viewer
            .map(IRecipeViewer::favorites)
            .orElse(mutableListOf())

    /** @return Pair<Boolean: Stack, Boolean: ToInv> */
    @JvmStatic
    fun matchesKey(mouseKey: Int): Pair<Boolean, Boolean>? {
        return viewer
            .map { viewer: IRecipeViewer -> viewer.matchesKey(mouseKey) }
            .orElse(null)
    }

    @JvmStatic
    val isCheatMode: Boolean
        get() = viewer.map(IRecipeViewer::isCheatMode).orElse(false)

    @JvmStatic
    fun addFavorite(stack: GenericStack) {
        viewer.ifPresent { viewer: IRecipeViewer -> viewer.addFavorite(stack) }
    }

    @JvmStatic
    fun setSearchText(text: String) {
        viewer.ifPresent { viewer: IRecipeViewer -> viewer.setSearch(text) }
    }
}
