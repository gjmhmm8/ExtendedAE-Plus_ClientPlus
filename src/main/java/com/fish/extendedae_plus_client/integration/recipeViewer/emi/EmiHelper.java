package com.fish.extendedae_plus_client.integration.recipeViewer.emi;

import appeng.api.stacks.GenericStack;
import appeng.integration.modules.emi.EmiStackHelper;
import com.fish.extendedae_plus_client.integration.recipeViewer.IHelperRecipeViewer;
import com.mojang.datafixers.util.Pair;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.runtime.EmiFavorites;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiHelper implements IHelperRecipeViewer {
    @Override
    public List<GenericStack> getHoveredStacks(double mouseX, double mouseY) {
        return EmiApi.getHoveredStack((int) mouseX, (int) mouseY, false).getStack().getEmiStacks()
                .stream().map(EmiStackHelper::toGenericStack).toList();
    }

    @Override
    public List<GenericStack> getHoveredStacks() {
        return EmiApi.getHoveredStack(false).getStack().getEmiStacks()
                .stream().map(EmiStackHelper::toGenericStack).toList();
    }

    @Override
    public List<GenericStack> getFavorites() {
        return EmiFavorites.favorites.stream()
                .map(EmiFavorite::getEmiStacks)
                .map(List::getFirst)
                .map(EmiStackHelper::toGenericStack)
                .toList();
    }

    @Override
    public @Nullable Pair<Boolean, Boolean> matchesKey(int mouseKey) {
        if (EmiConfig.cheatOneToCursor.matchesMouse(mouseKey))
            return new Pair<>(false, false);
        else if (EmiConfig.cheatOneToInventory.matchesMouse(mouseKey))
            return new Pair<>(false, true);
        else if (EmiConfig.cheatStackToCursor.matchesMouse(mouseKey))
            return new Pair<>(true, false);
        else if (EmiConfig.cheatStackToInventory.matchesMouse(mouseKey))
            return new Pair<>(true, true);
        else return null;
    }

    @Override
    public boolean isCheatMode() {
        return EmiApi.isCheatMode();
    }

    @Override
    public void addFavorite(GenericStack stack) {
        EmiFavorites.addFavorite(EmiStackHelper.toEmiStack(stack));
    }

    @Override
    public void setSearch(String text) {
        EmiApi.setSearchText(text);
    }
}
