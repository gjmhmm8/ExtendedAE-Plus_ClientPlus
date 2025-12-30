package com.fish.extendedae_plus_client.mixin.core.ae;

import appeng.api.stacks.GenericStack;
import appeng.integration.modules.itemlists.EncodingHelper;
import com.fish.extendedae_plus_client.config.EAEPCConfig;
import com.google.common.math.LongMath;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(EncodingHelper.class)
public class MixinEncoding {

    @Inject(method = "addOrMerge", at = @At("HEAD"), cancellable = true)
    private static void onTransferAdding(List<GenericStack> stacks, GenericStack newStack, CallbackInfo ci) {
        if (!Screen.hasShiftDown()) return;
        switch (EAEPCConfig.modeEncodingTransfer.get()) {
            case INDEPENDENCE -> {
                stacks.add(newStack);
                ci.cancel();
            }
            case MERGE_ADJACENCY -> {
                var existingStack = stacks.isEmpty() ? null : stacks.getLast();
                if (Objects.equals(existingStack, newStack)) {
                    var newAmount = LongMath.saturatedAdd(existingStack.amount(), newStack.amount());
                    stacks.removeLast();
                    stacks.addLast(new GenericStack(newStack.what(), newAmount));

                    var overflow = newStack.amount() - (newAmount - existingStack.amount());
                    if (overflow > 0) stacks.add(new GenericStack(newStack.what(), overflow));
                } else stacks.add(newStack);

                ci.cancel();
            }
        }
    }
}
