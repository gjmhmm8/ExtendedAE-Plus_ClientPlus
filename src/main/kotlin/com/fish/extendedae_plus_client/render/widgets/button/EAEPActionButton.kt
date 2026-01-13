package com.fish.extendedae_plus_client.render.widgets.button

import java.util.function.Consumer

open class EAEPActionButton(override val action: EAEPActionItems, onPress: Consumer<EAEPActionItems?>) : EAEPButton(
    { button: EAEPButton -> onPress.accept(button.action) }
) {
    init {
        this.updateTooltip()
    }
}
