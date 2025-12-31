package com.fish.extendedae_plus_client.integration.recipeViewer;

import appeng.api.stacks.GenericStack;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmptyHelper implements IRecipeViewer {
    @Override
    public List<GenericStack> getHoveredStacks(double mouseX, double mouseY) {
        return List.of();
    }

    @Override
    public List<GenericStack> getHoveredStacks() {
        return List.of();
    }

    @Override
    public List<GenericStack> getFavorites() {
        return List.of();
    }

    @Override
    public @Nullable Pair<Boolean, Boolean> matchesKey(int mouseKey) {
        return null;
    }

    @Override
    public void addFavorite(GenericStack stack) {
    }

    @Override
    public void setSearch(String text) {

    }
}
