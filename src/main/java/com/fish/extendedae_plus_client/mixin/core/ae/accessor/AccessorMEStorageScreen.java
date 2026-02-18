package com.fish.extendedae_plus_client.mixin.core.ae.accessor;

import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.widgets.AETextField;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperSearchField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = MEStorageScreen.class, remap = false)
public interface AccessorMEStorageScreen extends HelperSearchField {
    @Accessor("searchField")
    AETextField getSearchField();

    @Invoker("setSearchText")
    void eaep$setSearchText(String text);
}
