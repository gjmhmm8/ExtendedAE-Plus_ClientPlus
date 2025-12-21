package com.fish.extendedae_plus_client.mixin.impl;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import com.fish.extendedae_plus_client.impl.CacheProvider;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public final class HelperPatternMoving {
    private final AEBaseScreen<?> host;

    private final Map<Long, Set<Integer>> cacheUsedSlots;
    private final List<Pair<Integer, PatternContainerRecord>> patterns;

    private int delay;
    private boolean moving;
    private boolean completed;

    public HelperPatternMoving(AEBaseScreen<?> host) {
        this.host = host;
        this.patterns = new ArrayList<>();
        this.cacheUsedSlots = new HashMap<>();
        this.delay = 10;
    }

    public void clearReservation() {
        this.cacheUsedSlots.clear();
    }

    public void movePattern() {
        if (completed) return;
        if (this.delay > 0) {
            this.delay--;
            return;
        }

        if (this.patterns.isEmpty() && this.host.getMenu().getCarried().isEmpty())
            this.filterPattern();
        if (this.patterns.isEmpty()) return;

        var info = this.patterns.getFirst();

        var stateLastHolding = this.moving;
        this.movePattern(info.getFirst(), info.getSecond());

        if ((stateLastHolding && !this.moving)
                || !(stateLastHolding || this.moving))
            this.patterns.removeFirst();

        if (this.patterns.isEmpty())
            this.completed = true;
    }

    private void filterPattern() {
        if (Minecraft.getInstance().player == null) return;
        this.clearReservation();

        var inv = Minecraft.getInstance().player.getInventory().items;
        for (int index = 0; index < inv.size(); index++) {
            ItemStack stack = inv.get(index);
            if (stack.isEmpty()) continue;

            var hashGroup = CacheProvider.findProvider(PatternDetailsHelper.decodePattern(
                    stack, Minecraft.getInstance().level));

            if (hashGroup == null) continue;

            var providerInfo = CacheProvider.getProviderList().get(hashGroup);
            if (providerInfo == null) continue;

            this.patterns.add(new Pair<>(index, providerInfo));
        }
    }

    private void movePattern(Integer slot, PatternContainerRecord providerInfo) {
        if (CacheProvider.getProviderList().isEmpty()) {
            this.moving = false;
            return;
        }

        if (!this.host.getMenu().getCarried().isEmpty() && this.moving) {
            int targetSlot = -1;
            var inventory = providerInfo.getInventory();
            long providerId = providerInfo.getServerId();

            var used = cacheUsedSlots.computeIfAbsent(providerId, k -> new HashSet<>());
            for (int i = 0; i < inventory.size(); i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) continue;
                if (used.contains(i)) continue;

                targetSlot = i;
                break;
            }

            if (targetSlot == -1) {
                this.moving = false;
                return;
            }

            PacketDistributor.sendToServer(new InventoryActionPacket(
                    InventoryAction.PICKUP_OR_SET_DOWN,
                    targetSlot,
                    providerId
            ));

            used.add(targetSlot);
            this.moving = false;
        } else {
            var menu = this.host.getMenu();
            var player = Minecraft.getInstance().player;
            var gameMode = Minecraft.getInstance().gameMode;

            if (player == null || gameMode == null
                    || slot < 0 || slot > menu.slots.size()) {
                this.moving = false;
                return;
            }
            if (!menu.getSlot(slot).hasItem()) {
                this.moving = false;
                return;
            }

            gameMode.handleInventoryMouseClick(
                    menu.containerId,
                    slot,
                    GLFW.GLFW_MOUSE_BUTTON_LEFT,
                    ClickType.PICKUP,
                    player
            );
            this.moving = true;
        }
    }
}