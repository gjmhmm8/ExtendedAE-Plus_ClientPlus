package com.fish.extendedae_plus_client;

import com.fish.extendedae_plus_client.util.UtilKeyBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = ExtendedAEPlusClient.MODID)
public final class EAEPCKeyMapping {
    private static final Set<Lazy<KeyMapping>> mappings = new HashSet<>();

    private static final String CATEGORY = UtilKeyBuilder.of(UtilKeyBuilder.keyCategory).buildRaw();

    public static final Lazy<KeyMapping> fillToSearchField = register(
            "fill_to_search_field",
            KeyConflictContext.GUI,
            GLFW.GLFW_KEY_F
    );

    private static Lazy<KeyMapping> register(String name,
                                             IKeyConflictContext keyConflictContext,
                                             InputConstants.Type inputType,
                                             int keyCode,
                                             String category) {
        var mapping = Lazy.of(() -> new KeyMapping(
                UtilKeyBuilder.of(UtilKeyBuilder.key)
                        .addStr(name)
                        .buildRaw(),
                keyConflictContext,
                inputType,
                keyCode,
                category)
        );
        mappings.add(mapping);
        return mapping;
    }

    private static Lazy<KeyMapping> register(String name,
                                             IKeyConflictContext keyConflictContext,
                                             int keyCode) {
        return register(name, keyConflictContext, InputConstants.Type.KEYSYM, keyCode, CATEGORY);
    }

    @SubscribeEvent
    private static void onKeyMappingReg(RegisterKeyMappingsEvent event) {
        mappings.stream().map(Lazy::get).forEach(event::register);
    }
}
