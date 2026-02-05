package com.fish.extendedae_plus_client.mixin.impl.helper;

import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

public interface HelperProvidersListS2CPacket {
    List<Long> getIds();

    List<String> getNames();

    List<Integer> getEmptySlots();
}
