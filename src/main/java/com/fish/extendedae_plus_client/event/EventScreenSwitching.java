package com.fish.extendedae_plus_client.event;

import appeng.client.gui.me.common.MEStorageScreen;
import appeng.core.network.serverbound.SwitchGuisPacket;
import appeng.menu.me.crafting.CraftingStatusMenu;
import com.fish.extendedae_plus_client.ExtendedAEPlusClient;
import com.fish.extendedae_plus_client.impl.CacheCrafting;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ExtendedAEPlusClient.MODID)
public class EventScreenSwitching {
    @SubscribeEvent
    private static void onGuiOpening(ScreenEvent.Opening event) {
        if (event.getCurrentScreen() != null) return;
        if (!(event.getNewScreen() instanceof MEStorageScreen<?>)) return;
        if (CacheCrafting.isEmpty()) return;
        CacheCrafting.setOpening(true);
        PacketDistributor.sendToServer(SwitchGuisPacket.openSubMenu(CraftingStatusMenu.TYPE));
    }
}
