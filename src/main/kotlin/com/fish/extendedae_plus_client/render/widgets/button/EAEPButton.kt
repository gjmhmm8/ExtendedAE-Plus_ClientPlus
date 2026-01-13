package com.fish.extendedae_plus_client.render.widgets.button

import appeng.client.gui.Icon
import appeng.client.gui.style.Blitter
import appeng.client.gui.widgets.IconButton
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer
import java.util.regex.Pattern
import kotlin.math.max

abstract class EAEPButton(onPress: Consumer<EAEPButton>) : IconButton({ button: Button ->
    if (button is EAEPButton) onPress.accept(button)
}) {
    init {
        this.updateTooltip()
    }

    override fun onPress() {
        super.onPress()
        this.updateTooltip()
    }

    protected fun updateTooltip() {
        if (this.nonnullAction.hasName()) this.message = this.buildMessage(
            this.nonnullAction.actionName,
            this.nonnullAction.tooltip
        )
    }

    abstract val action: EAEPActionItems?
    
    private val nonnullAction: EAEPActionItems
        get() = this.action ?: EAEPActionItems.BACKING_OUT

    override fun getIcon(): Icon {
        return this.nonnullAction.aeIcon
    }

    protected val iconBlitter: Blitter
        get() = this.nonnullAction.iconBlitter

    protected fun buildMessage(i18nName: Component, i18nTooltip: Component?): Component {
        val name = i18nName.string
        if (i18nTooltip == null) {
            return Component.literal(name)
        } else {
            var value = i18nTooltip.string
            value = PATTERN_NEW_LINE.matcher(value).replaceAll("\n")
            val sb = StringBuilder(value)
            var i = max(sb.lastIndexOf("\n"), 0)

            while (i + 30 < sb.length && (sb.lastIndexOf(" ", i + 30).also { i = it }) != -1) {
                sb.replace(i, i + 1, "\n")
            }

            return Component.literal(name + "\n" + sb)
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partial: Float) {
        if (this.visible) {
            val blitter = this.iconBlitter
            val item = this.itemOverlay

            if (this.isHalfSize) {
                this.width = 8
                this.height = 8
            }

            val yOffset = if (isHovered()) 1 else 0

            if (this.isHalfSize) {
                if (!isDisableBackground) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.blitter.dest(x, y).zOffset(10).blit(guiGraphics)
                }
                if (item != null) {
                    guiGraphics.renderItem(ItemStack(item), x, y, 0, 20)
                } else {
                    if (!this.active) blitter.opacity(0.5f)
                    blitter.dest(x, y).zOffset(20).blit(guiGraphics)
                }
            } else {
                if (!isDisableBackground) {
                    val bgIcon = if (isHovered())
                        Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER
                    else
                        if (isFocused) Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS else Icon.TOOLBAR_BUTTON_BACKGROUND

                    bgIcon.blitter
                        .dest(x - 1, y + yOffset, 18, 20)
                        .zOffset(2)
                        .blit(guiGraphics)
                }
                if (item != null) guiGraphics.renderItem(ItemStack(item), x, y + 1 + yOffset, 0, 3)
                else blitter.dest(x, y + 1 + yOffset).zOffset(3).blit(guiGraphics)
            }
        }
    }

    companion object {
        protected val PATTERN_NEW_LINE: Pattern = Pattern.compile("\\n", Pattern.LITERAL)
    }
}
