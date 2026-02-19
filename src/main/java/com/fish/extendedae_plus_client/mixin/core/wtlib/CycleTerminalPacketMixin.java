package com.fish.extendedae_plus_client.mixin.core.wtlib;

import com.fish.extendedae_plus_client.mixin.impl.helper.HelperPatternMoving;
import de.mari_023.ae2wtlib.networking.c2s.CycleTerminalPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CycleTerminalPacket.class, remap = false)
public class CycleTerminalPacketMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(boolean reversed, CallbackInfo ci) {
        if (HelperPatternMoving.INSTANCE != null) {
            HelperPatternMoving.INSTANCE.onClose();
        }
    }
}

