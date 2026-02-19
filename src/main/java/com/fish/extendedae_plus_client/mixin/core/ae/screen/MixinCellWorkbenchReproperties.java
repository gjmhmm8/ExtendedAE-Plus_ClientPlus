package com.fish.extendedae_plus_client.mixin.core.ae.screen;

import appeng.client.gui.implementations.CellWorkbenchScreen;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.CellWorkbenchMenu;
import appeng.menu.slot.CellPartitionSlot;
import com.fish.extendedae_plus_client.config.EAEPCConfig;
import com.fish.extendedae_plus_client.integration.ContextModLoaded;
import com.fish.extendedae_plus_client.mixin.core.ftbLibrary.accessor.AccessorScreenBase;
import com.fish.extendedae_plus_client.mixin.core.ftbLibrary.accessor.AccessorScreenNBTEditor;
import dev.ftb.mods.ftblibrary.nbtedit.NBTEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CellWorkbenchScreen.class, remap = false)
public class MixinCellWorkbenchReproperties extends UpgradeableScreen<CellWorkbenchMenu> {
    public MixinCellWorkbenchReproperties(CellWorkbenchMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @SuppressWarnings("MixinAnnotationTarget")
    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void onSlotClick(@Nullable Slot slotUnchecked,
                             int slotIdx,
                             int mouseButton,
                             ClickType clickType,
                             CallbackInfo ci) {
        if (!ContextModLoaded.ftbLibrary.isLoaded()) return;
        if (!(slotUnchecked instanceof CellPartitionSlot slot)) return;
        if (!Minecraft.getInstance().options.keyPickItem.matchesMouse(mouseButton)) return;
        if (!EAEPCConfig.itemEditingTiggerMode.get().shouldTigger()) return;

        this.eaep$editNBT(slot);
        ci.cancel();
    }

    @Unique
    private void eaep$editNBT(CellPartitionSlot slot) {
        var stack = slot.getItem();
        if (stack.isEmpty()) return;

        var info = new CompoundTag();
        info.putString("type", "item");
        info.put("text", new ListTag());

        var data = stack.save(new CompoundTag());

        (new NBTEditorScreen(info, data, (accepted, responseTag) -> {
            if (!accepted) return;
            slot.setFilterTo(ItemStack.of(responseTag));
        }) {
            @Override
            public void closeGui() {
                Minecraft mc = Minecraft.getInstance();
                double mx = mc.mouseHandler.xpos();
                double my = mc.mouseHandler.ypos();

                var screen = (NBTEditorScreen) this;
                var accessor = (AccessorScreenNBTEditor) screen;

                mc.setScreen(this.getPrevScreen());
                GLFW.glfwSetCursorPos(mc.getWindow().getWindow(), mx, my);

                ((AccessorScreenBase) screen).getModalPanels().clear();
                this.onClosed();

                accessor.getCallback().handle(accessor.isAccepted(),
                        ((AccessorScreenNBTEditor.AccessorButtonNBTMap) accessor.getButtonRoot()).getMap());
            }
        }).openGui();
    }
}

