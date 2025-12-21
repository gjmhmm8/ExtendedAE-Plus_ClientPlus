package com.fish.extendedae_plus_client.event;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.core.AEConfig;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import com.fish.extendedae_plus_client.ExtendedAEPlusClient;
import com.fish.extendedae_plus_client.integration.recipeViewer.HelperRecipeViewer;
import com.fish.extendedae_plus_client.mixin.impl.HelperSearchField;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

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

        var pulled = HelperRecipeViewer.getPulled(event.getButton());
        if (pulled != null) {
            InventoryAction action;
            if (pulled.getFirst() && pulled.getSecond())
                action = InventoryAction.SHIFT_CLICK;
            else if (pulled.getFirst())
                action = InventoryAction.PICKUP_OR_SET_DOWN;
            else if (pulled.getSecond()) // 这里没有对应的action
                action = InventoryAction.SHIFT_CLICK;
            else action = InventoryAction.PICKUP_SINGLE;

            menu.handleInteraction(serial, action);

            isPulled = true;
            return;
        }

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            menu.handleInteraction(serial, InventoryAction.CRAFT_ITEM);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyPressedPre(ScreenEvent.KeyPressed.Pre event) {
        if (Minecraft.getInstance().player == null) return;
        if (event.getKeyCode() == GLFW.GLFW_KEY_F) {
            // 仅当鼠标确实悬停在 JEI 配料上时触发
            // 大概会在一格有多个(?)stack的时候出bug, 但是真的会有那种时候吗?
            GenericStack stack = HelperRecipeViewer.getHoveredStacks().getFirst();
            if (stack == null) return;
            String name = stack.what().getDisplayName().getString();

            // 写入 AE2 终端的搜索框
            if (AEConfig.instance().isUseExternalSearch()) {
                HelperRecipeViewer.setSearchText(name);
            } else if (Minecraft.getInstance().screen instanceof HelperSearchField helper) {
                helper.getSearchField().setValue(name);
                helper.eaep$setSearchText(name);
                event.setCanceled(true);
            }
        }
    }

    private static @Nullable Long findHoveredStackSerial(MEStorageMenu menu) {
        if (menu.getClientRepo() == null) return null;

        List<GenericStack> stacks = HelperRecipeViewer.getHoveredStacks();
        GenericStack stack = stacks.isEmpty() ? null : stacks.getFirst();
        if (stack == null) return null;
        if (!AEKeyType.items().equals(stack.what().getType())) return null;

        for (GridInventoryEntry entry : menu.getClientRepo().getAllEntries()) {
            if (!stack.what().equals(entry.getWhat())) continue;

            return entry.getSerial();
        }
        return null;
    }
}
