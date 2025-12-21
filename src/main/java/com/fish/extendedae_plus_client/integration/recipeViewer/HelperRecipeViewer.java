package com.fish.extendedae_plus_client.integration.recipeViewer;

import appeng.api.stacks.GenericStack;
import com.fish.extendedae_plus_client.integration.ContextModLoaded;
import com.fish.extendedae_plus_client.integration.recipeViewer.emi.EmiHelper;
import com.fish.extendedae_plus_client.integration.recipeViewer.jei.JeiHelper;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class HelperRecipeViewer {
    private static IHelperRecipeViewer activeViewer;

    public static void init() {
        if (ContextModLoaded.emi.isLoaded()) activeViewer = new EmiHelper();
        else if (ContextModLoaded.jei.isLoaded()) activeViewer = new JeiHelper();
        else activeViewer = new EmptyHelper();
    }

    public static Optional<IHelperRecipeViewer> getViewer() {
        if (activeViewer == null) init();
        return Optional.ofNullable(activeViewer);
    }

    public static List<GenericStack> getHoveredStacks() {
        return getViewer().map(IHelperRecipeViewer::getHoveredStacks).orElse(List.of());
    }

    public static List<GenericStack> getFavorites() {
        return getViewer().map(IHelperRecipeViewer::getFavorites).orElse(List.of());
    }

    public static @Nullable Pair<Boolean, Boolean> getPulled(int mouseKey) {
        return getViewer().map(viewer -> viewer.getPulled(mouseKey)).orElse(null);
    }

    public static boolean isCheatMode() {
        return getViewer().map(IHelperRecipeViewer::isCheatMode).orElse(false);
    }

    public static void addFavorite(GenericStack stack) {
        getViewer().ifPresent(viewer -> viewer.addFavorite(stack));
    }

    public static void setSearchText(String text) {
        getViewer().ifPresent(viewer -> viewer.setSearch(text));
    }
}
