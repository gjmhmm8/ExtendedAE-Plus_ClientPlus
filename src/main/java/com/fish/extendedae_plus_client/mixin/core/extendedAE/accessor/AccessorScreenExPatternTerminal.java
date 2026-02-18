package com.fish.extendedae_plus_client.mixin.core.extendedAE.accessor;

import appeng.client.gui.widgets.AETextField;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperSearchField;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiExPatternTerminal.class, remap = false)
public interface AccessorScreenExPatternTerminal extends HelperSearchField {
    @Accessor("searchField")
    AETextField getSearchField();
}
