package com.fish.extendedae_plus_client.mixin.core.ae.accessor;

import appeng.api.networking.security.IActionHost;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.menu.AEBaseMenu;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperAEBaseMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AEBaseMenu.class, remap = false)
public interface AccessorAEBaseMenu extends HelperAEBaseMenu {
    @Invoker("getActionHost")
    IActionHost eaep$getActionHost();
}
