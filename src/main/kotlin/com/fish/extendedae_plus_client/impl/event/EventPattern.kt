package com.fish.extendedae_plus_client.impl.event

import appeng.api.crafting.PatternDetailsHelper
import com.fish.extendedae_plus_client.ExtendedAEPlusClient
import com.fish.extendedae_plus_client.impl.cache.CacheProvider
import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

@EventBusSubscriber(modid = ExtendedAEPlusClient.MODID, value = [Dist.CLIENT])
object EventPattern {
    @SubscribeEvent(priority = EventPriority.HIGH)
    fun onPatternSRC(event: PlayerInteractEvent.RightClickItem){
        if(Minecraft.getInstance().player==null)return
        Minecraft.getInstance().player?.let { if(!(it.isShiftKeyDown))return }
        val patternDetails=PatternDetailsHelper.decodePattern(event.itemStack, Minecraft.getInstance().player?.level());
        patternDetails?.let { CacheProvider.unmarkPattern(it) };
    }
}