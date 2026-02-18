package com.fish.extendedae_plus_client.impl.event

import com.fish.extendedae_plus_client.impl.EAEEncodingHelper
import com.fish.extendedae_plus_client.impl.cache.CacheCrafting
import com.fish.extendedae_plus_client.impl.cache.CacheCuttingKnife
import com.fish.extendedae_plus_client.impl.cache.CacheProvider
import net.minecraftforge.event.GameShuttingDownEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object EventAutoClear {
    @Suppress("unused")
    @SubscribeEvent
    fun onGameClosing(event: GameShuttingDownEvent) {
        this.clearAll()
    }

    private fun clearAll() {
        CacheCrafting.clear()
        CacheProvider.clearProvider()
        CacheProvider.clearPattern()
        CacheCuttingKnife.isHandlingBlockCopies = false
        EAEEncodingHelper.clearRecipeKeywords()
        EAEEncodingHelper.closeConfig()
    }
}