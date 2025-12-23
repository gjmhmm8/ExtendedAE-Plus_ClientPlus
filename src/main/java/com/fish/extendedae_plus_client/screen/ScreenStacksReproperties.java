package com.fish.extendedae_plus_client.screen;

import appeng.api.stacks.GenericStack;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.me.common.ClientDisplaySlot;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.widgets.ConfirmableTextField;
import appeng.client.gui.widgets.TabButton;
import appeng.client.gui.widgets.ToggleButton;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.fish.extendedae_plus_client.impl.ConstantCustomData;
import com.fish.extendedae_plus_client.util.UtilKeyBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

public class ScreenStacksReproperties<TMenu extends PatternEncodingTermMenu>
        extends AESubScreen<TMenu, PatternEncodingTermScreen<TMenu>> {
    public static final String STYLE_PATH = "/screens/extendedae_plus_client/stacks_reproperties.json";

    private final ItemStack stack;
    private final Consumer<ItemStack> confirmer;

    private final ToggleButton buttonAutoCompletion;
    private final ConfirmableTextField fieldRename;
    private boolean autoCompletion;

    public ScreenStacksReproperties(PatternEncodingTermScreen<TMenu> parent,
                                    ItemStack stack,
                                    Consumer<ItemStack> confirmer,
                                    boolean primaryOutput) {
        super(parent, STYLE_PATH);
        this.stack = stack;
        this.confirmer = confirmer;

        this.widgets.addButton("button_save", GuiText.Set.text(), this::confirm);
        this.widgets.add("button_back",
                new TabButton(Icon.BACK,
                        getMenu().getHost().getMainMenuIcon().getHoverName(),
                        btn -> returnToParent()
                )
        );

        if (primaryOutput) {
            this.buttonAutoCompletion = new ToggleButton(
                    Icon.SCHEDULING_DEFAULT,
                    Icon.ARROW_RIGHT,
                    this::toggleAutoCompletion
            );
            this.buttonAutoCompletion.setTooltipOff(List.of(UtilKeyBuilder.of(UtilKeyBuilder.screen)
                            .addStr("auto_completion")
                            .addStr("off")
                            .build()));
            this.buttonAutoCompletion.setTooltipOn(List.of(
                    UtilKeyBuilder.of(UtilKeyBuilder.screen)
                            .addStr("auto_completion")
                            .addStr("on")
                            .build(),
                    UtilKeyBuilder.of(UtilKeyBuilder.screen)
                            .addStr("auto_completion")
                            .addStr("description")
                            .build(),
                    UtilKeyBuilder.of(UtilKeyBuilder.screen)
                            .addStr("auto_completion")
                            .addStr("only_completion")
                            .build())
            );
            this.widgets.add("button_auto_completion", this.buttonAutoCompletion);

            this.autoCompletion = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .contains(ConstantCustomData.autoCompletable.get());
        } else this.buttonAutoCompletion = null;

        var font = Minecraft.getInstance().font;
        var fieldStyle = this.getStyle().getWidget("field_stacks_rename");
        this.fieldRename = new ConfirmableTextField(this.getStyle(),
                font,
                fieldStyle.getLeft() == null ? 0 : fieldStyle.getLeft(),
                fieldStyle.getTop() == null ? 0 : fieldStyle.getTop(),
                fieldStyle.getWidth(),
                fieldStyle.getHeight());
        this.fieldRename.setBordered(false);
        this.fieldRename.setMaxLength(50);
        this.fieldRename.setTextColor(0xFFFFFF);
        this.fieldRename.setSelectionColor(0xFF000080);
        this.fieldRename.setVisible(true);
        this.fieldRename.setOnConfirm(this::confirm);
        this.fieldRename.setValue(stack.getHoverName().getString());
        this.fieldRename.setPlaceholder(stack.getHoverName());
        this.widgets.add("field_stacks_rename", this.fieldRename);

        this.addClientSideSlot(new ClientDisplaySlot(GenericStack.fromItemStack(stack)),
                SlotSemantics.MACHINE_OUTPUT);
    }

    @Override
    protected void init() {
        super.init();

        this.setInitialFocus(this.fieldRename);
        this.setSlotsHidden(SlotSemantics.TOOLBOX, true);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        if (this.buttonAutoCompletion != null)
            this.buttonAutoCompletion.setState(this.autoCompletion);
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT || !this.fieldRename.isMouseOver(xCoord, yCoord))
            return super.mouseClicked(xCoord, yCoord, button);
        this.fieldRename.setValue("");
        this.setFocused(this.fieldRename);
        return true;
    }

    private void confirm() {
        var newStack = this.stack.copy();

        var name = this.fieldRename.getValue();
        if (!(name.isBlank()
                || name.equals(newStack.getOrDefault(DataComponents.ITEM_NAME, Component.empty()).getString())
                || name.equals(newStack.getItem().getName(newStack).getString()))) {
            newStack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        } else newStack.remove(DataComponents.CUSTOM_NAME);

        CustomData.update(DataComponents.CUSTOM_DATA, newStack, data -> {
            if (this.autoCompletion) data.putBoolean(ConstantCustomData.autoCompletable.get(), true);
            else data.remove(ConstantCustomData.autoCompletable.get());
        });

        this.confirmer.accept(newStack);
        this.returnToParent();
    }

    private void toggleAutoCompletion(boolean state) {
        this.autoCompletion = state;
    }

    @Override
    public void onClose() {
        this.returnToParent();
    }
}
