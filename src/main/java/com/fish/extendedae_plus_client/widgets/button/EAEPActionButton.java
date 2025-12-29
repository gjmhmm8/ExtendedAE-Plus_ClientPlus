package com.fish.extendedae_plus_client.widgets.button;

import java.util.function.Consumer;

public class EAEPActionButton extends EAEPButton {
    protected final EAEPActionItems action;

    public EAEPActionButton(EAEPActionItems action, Consumer<EAEPActionItems> onPress) {
        super(button -> onPress.accept(button.getAction()));
        this.action = action;
        this.updateTooltip();
    }

    @Override
    public EAEPActionItems getAction() {
        return this.action;
    }
}
