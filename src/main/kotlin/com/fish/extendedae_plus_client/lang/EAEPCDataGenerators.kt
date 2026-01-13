package com.fish.extendedae_plus_client.lang

import com.fish.extendedae_plus_client.ExtendedAEPlusClient
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(modid = ExtendedAEPlusClient.MODID)
object EAEPCDataGenerators {
    @SubscribeEvent
    private fun register(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput

        generator.addProvider(event.includeClient(), LangEN(output))
        generator.addProvider(event.includeClient(), LangZH(output))
    }
}
