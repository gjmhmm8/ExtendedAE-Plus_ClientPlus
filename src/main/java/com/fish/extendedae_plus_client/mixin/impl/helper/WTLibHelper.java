package com.fish.extendedae_plus_client.mixin.impl.helper;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.client.Hotkeys;
import appeng.core.network.serverbound.HotkeyPacket;
import appeng.menu.AEBaseMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.me.common.MEStorageMenu;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;
import de.mari_023.ae2wtlib.wut.WTDefinitions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class WTLibHelper {
    public static final WTDefinition PATTERN_ACCESS = WTDefinitions.PATTERN_ACCESS;
    public static WTDefinition EX_PATTERN_ACCESS = WTDefinition.of("ex_pattern_access");
    public static WTDefinition PATTERN_ENCODING = WTDefinitions.PATTERN_ENCODING;

    public static WTDefinition last_terminal = null;

    public static ItemMenuHostLocator getLocator() {
        var player = Minecraft.getInstance().player;
        if (player == null) return null;
        final AbstractContainerMenu containerMenu = player.containerMenu;
        if (!(containerMenu instanceof AEBaseMenu aeMenu))
            return null;

        var host=((HelperAEBaseMenu)aeMenu).eaep$getActionHost();
        if (!(host instanceof ItemMenuHost<?> iHost && iHost.getLocator() instanceof ItemMenuHostLocator locator))
            return null;
        return locator;
    }

    public static WTDefinition getCurWT() {
        var player = Minecraft.getInstance().player;
        var locator = getLocator();
        if (player == null || locator == null) return null;
        return WTDefinition.ofOrNull(locator.locateItem(player));
    }

    public static void switchOnce(boolean reversed) {
        PacketDistributor.sendToServer(new CycleTerminalPacket(reversed));
    }


    public static boolean openTerminalCyc(WTDefinition definition) {
        if (definition == null) return false;
        var player = Minecraft.getInstance().player;
        if (player == null) return false;

        var locator = getLocator();
        if (locator == null) return false;

        ItemStack stack = locator.locateItem(player);
        if (stack == null || stack.isEmpty()) return false;

        WTDefinition cur = WTDefinition.ofOrNull(stack);
        if (cur == null) return false;

        // 目标必须存在于当前 stack
        if (stack.get(definition.componentType()) == null) return false;

        last_terminal=cur;

        if (definition.equals(cur)) return true;

        // 用“可用终端序列”算最短路
        var terminals = WTDefinition.wirelessTerminalList;
        java.util.ArrayList<WTDefinition> available = new java.util.ArrayList<>();
        for (var def : terminals) {
            if (stack.get(def.componentType()) != null) available.add(def);
        }
        if (available.size() <= 1) return false;

        int curPos = available.indexOf(cur);
        int targetPos = available.indexOf(definition);
        if (curPos < 0 || targetPos < 0) return false;

        int n = available.size();
        int forward = (targetPos - curPos + n) % n;   // reversed=false
        int backward = (curPos - targetPos + n) % n;  // reversed=true

        boolean reversed = backward < forward;
        int steps = Math.min(forward, backward);

        for (int i = 0; i < steps; i++) {
            switchOnce(reversed);
        }
        return true; // 不做 getCurWT 校验，避免延迟导致误判
    }

    public static void openTerminalHK(WTDefinition terminal) {
        last_terminal = getCurWT();
        if (last_terminal == null) return;
        var hotkey = Hotkeys.getHotkeyMapping(terminal.hotkeyName());
        if (hotkey != null) {
            PacketDistributor.sendToServer(new HotkeyPacket(hotkey));
        }
    }

    public static void goBackCyc() {
        openTerminalCyc(last_terminal);
        last_terminal = null;
    }

}
