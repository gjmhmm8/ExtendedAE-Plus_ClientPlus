package com.fish.extendedae_plus_client.mixin.core.ae.accessor;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AEBaseScreen.class, remap = false)
public interface AccessorAEBaseScreen {
    @Invoker("switchToScreen")
    void eaep$switchToScreen(AEBaseScreen<?> screen);
}


