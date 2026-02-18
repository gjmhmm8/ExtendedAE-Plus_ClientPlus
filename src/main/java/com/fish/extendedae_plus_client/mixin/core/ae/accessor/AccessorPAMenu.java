package com.fish.extendedae_plus_client.mixin.core.ae.accessor;

import appeng.menu.implementations.PatternAccessTermMenu;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperPAMenu;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PatternAccessTermMenu.class, remap = false)
public interface AccessorPAMenu extends HelperPAMenu {
    @Accessor("byId")
    Long2ObjectOpenHashMap getById();
}
