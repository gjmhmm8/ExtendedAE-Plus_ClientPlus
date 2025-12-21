package com.fish.extendedae_plus_client.integration.recipeViewer;

import appeng.api.stacks.GenericStack;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IHelperRecipeViewer {
    List<GenericStack> getHoveredStacks(double mouseX, double mouseY);

    List<GenericStack> getHoveredStacks();

    List<GenericStack> getFavorites();

    @Nullable
    Pair<Boolean, Boolean> getPulled(int mouseKey);

    default boolean isCheatMode() {
        return false;
    }

    void addFavorite(GenericStack stack);

    void setSearch(String text);
}
