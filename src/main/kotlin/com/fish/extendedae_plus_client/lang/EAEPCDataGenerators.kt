package com.fish.extendedae_plus_client.lang

import com.fish.extendedae_plus_client.ExtendedAEPlusClient
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = ExtendedAEPlusClient.MODID)
object EAEPCDataGenerators {
    @SubscribeEvent
    fun register(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput

        generator.addProvider(event.includeClient(), LangEN(output))
        generator.addProvider(event.includeClient(), LangZH(output))
    }
}
