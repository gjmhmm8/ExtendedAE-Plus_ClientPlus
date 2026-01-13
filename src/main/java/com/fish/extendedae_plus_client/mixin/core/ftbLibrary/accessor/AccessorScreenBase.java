package com.fish.extendedae_plus_client.mixin.core.ftbLibrary.accessor;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(BaseScreen.class)
public interface AccessorScreenBase {
    @Accessor("modalPanels")
    Deque<ModalPanel> getModalPanels();
}
