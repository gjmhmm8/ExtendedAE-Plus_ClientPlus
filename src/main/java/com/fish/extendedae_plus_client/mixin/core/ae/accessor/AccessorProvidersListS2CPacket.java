package com.fish.extendedae_plus_client.mixin.core.ae.accessor;

import com.extendedae_plus.network.provider.ProvidersListS2CPacket;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperProvidersListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ProvidersListS2CPacket.class, remap = false)
public interface AccessorProvidersListS2CPacket extends HelperProvidersListS2CPacket {
    @Accessor("ids")
    List<Long> getIds();

    @Accessor("names")
    List<String> getNames();

    @Accessor("emptySlots")
    List<Integer> getEmptySlots();
}

