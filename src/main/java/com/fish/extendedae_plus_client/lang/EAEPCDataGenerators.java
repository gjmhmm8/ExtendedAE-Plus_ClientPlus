package com.fish.extendedae_plus_client.lang;

import com.fish.extendedae_plus_client.ExtendedAEPlusClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ExtendedAEPlusClient.MODID)
public class EAEPCDataGenerators {
    @SubscribeEvent
    private static void register(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new LangEN(output));
        generator.addProvider(event.includeClient(), new LangZH(output));
    }
}
