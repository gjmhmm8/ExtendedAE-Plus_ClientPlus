package com.fish.extendedae_plus_client.mixin.core.eaep;

import com.extendedae_plus.network.provider.ProvidersListS2CPacket;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperPatternMoving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProvidersListS2CPacket.class, remap = false)
public class ProvidersListS2CPacketMixin {
    @Inject(method = "handleClient", at = @At("HEAD"), cancellable = true)
    private static void handle(ProvidersListS2CPacket msg, CallbackInfo ci) {
        if (HelperPatternMoving.uploadedGroup != null) {
            HelperPatternMoving.eaepPacketHandler(msg);
            ci.cancel();
        }
    }
}

