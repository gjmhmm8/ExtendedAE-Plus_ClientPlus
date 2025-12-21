package com.fish.extendedae_plus_client.mixin.impl;

import appeng.client.gui.widgets.AETextField;

public interface HelperSearchField {
    AETextField getSearchField();

    default void eaep$setSearchText(String text) {
        this.getSearchField().setValue(text);
    }
}
