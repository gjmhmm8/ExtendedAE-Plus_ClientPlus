package com.fish.extendedae_plus_client.render.screen

import appeng.api.stacks.GenericStack
import appeng.client.gui.AEBaseScreen
import appeng.client.gui.AESubScreen
import appeng.client.gui.Icon
import appeng.client.gui.me.common.ClientDisplaySlot
import appeng.client.gui.widgets.ConfirmableTextField
import appeng.client.gui.widgets.TabButton
import appeng.client.gui.widgets.ToggleButton
import appeng.core.localization.GuiText
import appeng.menu.SlotSemantics
import appeng.menu.me.common.MEStorageMenu
import com.fish.extendedae_plus_client.impl.ConstantCustomData
import com.fish.extendedae_plus_client.util.UtilKeyBuilder
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer

class ScreenStacksReproperties<TMenu : MEStorageMenu>(
    parent: AEBaseScreen<TMenu>,
    private val stack: ItemStack,
    private val confirmer: Consumer<ItemStack>,
    primaryOutput: Boolean
) : AESubScreen<TMenu, AEBaseScreen<TMenu>>(parent, PATH_STYLE) {
    private val buttonAutoCompletion: ToggleButton?
    private val fieldRename: ConfirmableTextField
    private var autoCompletion = false

    init {
        this.widgets.addButton("button_save", GuiText.Set.text(), ::confirm)
        this.widgets.add(
            "button_back",
            TabButton(
                Icon.ARROW_LEFT,
                getMenu().host.mainMenuIcon.hoverName,
                Button.OnPress { this.returnToParent() }
            )
        )

        if (primaryOutput) {
            this.buttonAutoCompletion = ToggleButton(
                Icon.SCHEDULING_DEFAULT,
                Icon.ARROW_RIGHT,
                ::toggleAutoCompletion
            )
            this.buttonAutoCompletion.setTooltipOff(
                listOf<Component>(
                    UtilKeyBuilder.of(UtilKeyBuilder.screen)
                        .addStr("auto_completion")
                        .addStr("off")
                        .build()
                )
            )
            this.buttonAutoCompletion.setTooltipOn(
                UtilKeyBuilder.of(UtilKeyBuilder.screen)
                    .addStr("auto_completion")
                    .newArrayList()
                    .buildInto("on")
                    .buildInto("description")
                    .buildInto("only_completion")
                    .get() as ArrayList<Component>
            )
            this.widgets.add("button_auto_completion", this.buttonAutoCompletion)

            this.autoCompletion = stack.tag?.getBoolean(ConstantCustomData.autoCompletable.get()) ?: false
        } else this.buttonAutoCompletion = null

        val fieldStyle = this.getStyle().getWidget("field_stacks_rename")
        this.fieldRename = ConfirmableTextField(
            this.getStyle(),
            this.font,
            fieldStyle.left ?: 0,
            fieldStyle.top ?: 0,
            fieldStyle.width,
            fieldStyle.height
        )
        this.fieldRename.setBordered(false)
        this.fieldRename.setMaxLength(50)
        this.fieldRename.setTextColor(0xFFFFFF)
        this.fieldRename.setSelectionColor(-0xffff80)
        this.fieldRename.isVisible = true
        this.fieldRename.setOnConfirm(::confirm)
        this.fieldRename.value = stack.hoverName.string
        this.fieldRename.placeholder = stack.hoverName
        this.widgets.add("field_stacks_rename", this.fieldRename)

        this.addClientSideSlot(
            ClientDisplaySlot(GenericStack.fromItemStack(stack)),
            SlotSemantics.MACHINE_OUTPUT
        )
    }

    override fun init() {
        super.init()
        this.setInitialFocus(this.fieldRename)
        this.setSlotsHidden(SlotSemantics.TOOLBOX, true)
    }

    override fun updateBeforeRender() {
        super.updateBeforeRender()
        this.buttonAutoCompletion?.setState(this.autoCompletion)
    }

    override fun mouseClicked(xCoord: Double, yCoord: Double, button: Int): Boolean {
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT
            || !this.fieldRename.isMouseOver(xCoord, yCoord))
            return super.mouseClicked(xCoord, yCoord, button)
        this.fieldRename.value = ""
        this.setFocused(this.fieldRename)
        return true
    }

    override fun onClose() {
        this.returnToParent()
    }

    private fun confirm() {
        val newStack = this.stack.copy()

        val name = this.fieldRename.value
        if (!(name.isBlank() || name == newStack.item.getName(newStack).string)) {
            newStack.setHoverName(Component.literal(name))
        } else {
            newStack.resetHoverName()
        }

        val data = newStack.orCreateTag
        if (this.autoCompletion) {
            data.putBoolean(ConstantCustomData.autoCompletable.get(), true)
        } else {
            data.remove(ConstantCustomData.autoCompletable.get())
        }
        if (data.isEmpty) {
            newStack.tag = null
        }

        this.confirmer.accept(newStack)
        this.returnToParent()
    }

    private fun toggleAutoCompletion(state: Boolean) {
        this.autoCompletion = state
    }

    companion object {
        const val PATH_STYLE = "/screens/extendedae_plus_client/stacks_reproperties.json"
    }
}