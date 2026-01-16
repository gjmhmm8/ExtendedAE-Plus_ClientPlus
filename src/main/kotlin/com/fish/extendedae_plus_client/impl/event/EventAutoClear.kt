package com.fish.extendedae_plus_client.impl.event

import com.fish.extendedae_plus_client.impl.AliasGetter
import com.fish.extendedae_plus_client.impl.cache.CacheCrafting
import com.fish.extendedae_plus_client.impl.cache.CacheCuttingKnife
import com.fish.extendedae_plus_client.impl.cache.CacheProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.GameShuttingDownEvent

@EventBusSubscriber
object EventAutoClear {
    @SubscribeEvent
    fun onGameClosing(event: GameShuttingDownEvent) {
        this.clearAll()
    }

    private fun clearAll() {
        CacheCrafting.clear()
        CacheProvider.clearProvider()
        CacheProvider.clearPattern()
        CacheCuttingKnife.isHandlingBlockCopies = false
        AliasGetter.clearRecipeKeywords()
        AliasGetter.closeConfig()
    }
}