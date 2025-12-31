package com.fish.extendedae_plus_client.integration.recipeViewer;

import appeng.api.stacks.GenericStack;
import com.fish.extendedae_plus_client.integration.ContextModLoaded;
import com.fish.extendedae_plus_client.integration.recipeViewer.emi.ViewerEmi;
import com.fish.extendedae_plus_client.integration.recipeViewer.jei.ViewerJei;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class HelperRecipeViewer {
    private static IRecipeViewer activeViewer;

    public static void init() {
        if (ContextModLoaded.emi.isLoaded()) activeViewer = new ViewerEmi();
        else if (ContextModLoaded.jei.isLoaded()) activeViewer = new ViewerJei();
        else activeViewer = new EmptyHelper();
    }

    public static Optional<IRecipeViewer> getViewer() {
        if (activeViewer == null) init();
        return Optional.ofNullable(activeViewer);
    }

    public static List<GenericStack> getHoveredStacks() {
        return getViewer().map(IRecipeViewer::getHoveredStacks).orElse(List.of());
    }

    public static List<GenericStack> getFavorites() {
        return getViewer().map(IRecipeViewer::getFavorites).orElse(List.of());
    }

    /// @return Pair<Boolean: Stack, Boolean: ToInv>
    public static @Nullable Pair<Boolean, Boolean> matchesKey(int mouseKey) {
        return getViewer().map(viewer -> viewer.matchesKey(mouseKey)).orElse(null);
    }

    public static boolean isCheatMode() {
        return getViewer().map(IRecipeViewer::isCheatMode).orElse(false);
    }

    public static void addFavorite(GenericStack stack) {
        getViewer().ifPresent(viewer -> viewer.addFavorite(stack));
    }

    public static void setSearchText(String text) {
        getViewer().ifPresent(viewer -> viewer.setSearch(text));
    }
}
