package com.fish.extendedae_plus_client.impl.event;

import appeng.client.gui.implementations.QuartzKnifeScreen;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.core.network.serverbound.SwitchGuisPacket;
import appeng.menu.me.crafting.CraftingStatusMenu;
import com.fish.extendedae_plus_client.ExtendedAEPlusClient;
import com.fish.extendedae_plus_client.impl.cache.CacheCrafting;
import com.fish.extendedae_plus_client.impl.cache.CacheCuttingKnife;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ExtendedAEPlusClient.MODID)
public class EventScreenSwitching {
    @SubscribeEvent
    private static void onGuiOpening(ScreenEvent.Opening event) {
        switch (event.getNewScreen()) {
            case MEStorageScreen<?> ignored -> handleMEStorageScreen(event);
            case QuartzKnifeScreen screen -> handleCuttingKnifeScreen(screen, event);
            case null, default -> {}
        }
    }

    private static void handleMEStorageScreen(ScreenEvent.Opening event) {
        if (event.getCurrentScreen() != null) return;
        if (CacheCrafting.isEmpty()) return;
        CacheCrafting.setOpening(true);
        PacketDistributor.sendToServer(SwitchGuisPacket.openSubMenu(CraftingStatusMenu.TYPE));
    }

    private static void handleCuttingKnifeScreen(QuartzKnifeScreen screen, ScreenEvent.Opening event) {
        if (!CacheCuttingKnife.isHandlingBlockCopies()) return;
        event.setCanceled(true);

        var connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        connection.send(new ServerboundContainerClosePacket(screen.getMenu().containerId));
    }
}
