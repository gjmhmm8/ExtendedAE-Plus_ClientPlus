package com.fish.extendedae_plus_client.impl.event

import appeng.client.gui.implementations.QuartzKnifeScreen
import appeng.client.gui.me.common.MEStorageScreen
import appeng.core.sync.network.NetworkHandler
import appeng.core.sync.packets.SwitchGuisPacket
import appeng.menu.me.crafting.CraftingStatusMenu
import com.fish.extendedae_plus_client.ExtendedAEPlusClient
import com.fish.extendedae_plus_client.impl.cache.CacheCrafting
import com.fish.extendedae_plus_client.impl.cache.CacheCuttingKnife
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ScreenEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = ExtendedAEPlusClient.MODID)
object EventScreenSwitching {
    @SubscribeEvent
    fun onGuiOpening(event: ScreenEvent.Opening) {
        when (event.newScreen) {
            is MEStorageScreen<*> -> handleMEStorageScreen(event)
            is QuartzKnifeScreen -> handleCuttingKnifeScreen(event)
        }
    }

    private fun handleMEStorageScreen(event: ScreenEvent.Opening) {
        if (event.currentScreen != null) return
        if (CacheCrafting.isEmpty) return
        CacheCrafting.isOpening = true
        NetworkHandler.instance().sendToServer(SwitchGuisPacket.openSubMenu(CraftingStatusMenu.TYPE))
    }

    private fun handleCuttingKnifeScreen(event: ScreenEvent.Opening) {
        if (!CacheCuttingKnife.isHandlingBlockCopies) return
        event.setCanceled(true)
        Minecraft.getInstance().player?.closeContainer()
    }
}
