package com.fish.extendedae_plus_client.impl.event;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.core.AEConfig;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import com.fish.extendedae_plus_client.ExtendedAEPlusClient;
import com.fish.extendedae_plus_client.config.EAEPCKeyMapping;
import com.fish.extendedae_plus_client.integration.recipeViewer.HelperRecipeViewer;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperSearchField;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ExtendedAEPlusClient.MODID, value = Dist.CLIENT)
public final class EventScreenActions {
    private static boolean isPulled;

    @SubscribeEvent
    public static void onMouseButtonPre(InputEvent.MouseButton.Pre event) {
        if (Minecraft.getInstance().player == null) return;
        if (Minecraft.getInstance().screen == null) return;

        if (!(Minecraft.getInstance().player.containerMenu instanceof MEStorageMenu menu)) return;

        if (HelperRecipeViewer.isCheatMode()) return;

        if (event.getAction() != GLFW.GLFW_PRESS) {
            if (isPulled) event.setCanceled(true);
            isPulled = false;
            return;
        }

        var serial = findHoveredStackSerial(menu);
        if (serial == null) return;

        var pulled = HelperRecipeViewer.matchesKey(event.getButton());
        if (pulled != null) {
            InventoryAction action;
            if (pulled.getFirst() && pulled.getSecond())
                action = InventoryAction.SHIFT_CLICK;
            else if (pulled.getFirst())
                action = InventoryAction.PICKUP_OR_SET_DOWN;
            else if (pulled.getSecond()) // 这里没有对应的 action
                action = InventoryAction.SHIFT_CLICK;
            else action = InventoryAction.PICKUP_SINGLE;

            menu.handleInteraction(serial, action);

            isPulled = true;
            return;
        }

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            menu.handleInteraction(serial, InventoryAction.AUTO_CRAFT);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyPressedPre(ScreenEvent.KeyPressed.Pre event) {
        if (Minecraft.getInstance().player == null) return;
        if (EAEPCKeyMapping.fillToSearchField.get().matches(event.getKeyCode(), event.getScanCode())) {
            // 增强功能, 现在可以检测所有EMIIngredient和screen里的ItemStack了
            // 大概会在一格有多个(?)stack的时候出bug, 但是真的会有那种时候吗?
            GenericStack stack = null;
            var stacks = HelperRecipeViewer.getHoveredStacks();
            if (!stacks.isEmpty()) stack = stacks.getFirst();
            if (stack == null && Minecraft.getInstance().screen instanceof AEBaseScreen<?> screen) {
                var slot = screen.getSlotUnderMouse();
                if (slot == null) return;
                stack = GenericStack.fromItemStack(slot.getItem());
            }
            if (stack == null) return;
            var name = stack.what().getDisplayName().getString();

            // 写入 AE2 终端的搜索框
            if (AEConfig.instance().isUseExternalSearch()) {
                HelperRecipeViewer.setSearchText(name);
            } else if (Minecraft.getInstance().screen instanceof HelperSearchField helper) {
                helper.getSearchField().setValue(name);
                helper.eaep$setSearchText(name);
            }
            event.setCanceled(true);
        }
    }

    private static @Nullable Long findHoveredStackSerial(MEStorageMenu menu) {
        if (menu.getClientRepo() == null) return null;

        var stacks = HelperRecipeViewer.getHoveredStacks();
        var stack = stacks.isEmpty() ? null : stacks.getFirst();
        if (stack == null) return null;
        if (!AEKeyType.items().equals(stack.what().getType())) return null;

        for (var entry : menu.getClientRepo().getAllEntries()) {
            if (!stack.what().equals(entry.getWhat())) continue;

            return entry.getSerial();
        }
        return null;
    }
}
