package com.fish.extendedae_plus_client.mixin.impl.helper;

import appeng.api.networking.security.IActionHost;
import appeng.menu.AEBaseMenu;
import com.fish.extendedae_plus_client.mixin.core.ae.accessor.AccessorAEBaseMenu;
import de.mari_023.ae2wtlib.networking.ClientNetworkManager;
import de.mari_023.ae2wtlib.networking.c2s.CycleTerminalPacket;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class WTLibHelper {
    public static final String PATTERN_ACCESS = "pattern_access";
    public static final String EX_PATTERN_ACCESS = "ex_pattern_access";
    public static final String PATTERN_ENCODING = "pattern_encoding";
    public static String lastTerminal = null;

    private WTLibHelper() {
    }

    private static IActionHost getActionHost() {
        var player = Minecraft.getInstance().player;
        if (player == null) return null;
        if (!(player.containerMenu instanceof AEBaseMenu aeMenu)) return null;
        return ((AccessorAEBaseMenu) aeMenu).eaep$getActionHost();
    }

    private static ItemStack getCurrentTerminalStack() {
        var actionHost = getActionHost();
        if (!(actionHost instanceof appeng.api.implementations.menuobjects.ItemMenuHost itemHost)) return ItemStack.EMPTY;
        return itemHost.getItemStack();
    }

    private static void switchOnce(boolean reversed) {
        ClientNetworkManager.sendToServer(new CycleTerminalPacket(reversed));
    }

    public static boolean openTerminalCyc(String definition) {
        if (definition == null || definition.isBlank()) return false;
        var stack = getCurrentTerminalStack();
        if (stack.isEmpty()) return false;

        String cur = WUTHandler.getCurrentTerminal(stack);
        if (cur == null || cur.isBlank()) return false;
        if (!WUTHandler.hasTerminal(stack, definition)) return false;

        lastTerminal = cur;
        if (definition.equals(cur)) return true;

        ArrayList<String> available = new ArrayList<>();
        for (var name : WUTHandler.terminalNames) {
            if (WUTHandler.hasTerminal(stack, name)) {
                available.add(name);
            }
        }
        if (available.size() <= 1) return false;

        int curPos = available.indexOf(cur);
        int targetPos = available.indexOf(definition);
        if (curPos < 0 || targetPos < 0) return false;

        int n = available.size();
        int forward = (targetPos - curPos + n) % n;
        int backward = (curPos - targetPos + n) % n;
        boolean reversed = backward < forward;
        int steps = Math.min(forward, backward);
        for (int i = 0; i < steps; i++) {
            switchOnce(reversed);
        }
        return true;
    }

    public static void openTerminalHK(String terminal) {
        openTerminalCyc(terminal);
    }

    public static void goBackCyc() {
        if (lastTerminal != null) {
            openTerminalCyc(lastTerminal);
            lastTerminal = null;
        }
    }
}
