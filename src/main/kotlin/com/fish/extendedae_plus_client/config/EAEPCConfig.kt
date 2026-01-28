package com.fish.extendedae_plus_client.config

import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.ModConfigSpec
import java.util.function.Consumer

object EAEPCConfig {
    val SPEC: ModConfigSpec

    @JvmField
    val autoPlateRepeat: ModConfigSpec.IntValue
    @JvmField
    val modeEncodingTransfer: ModConfigSpec.EnumValue<ModeEncodingTransfer>

    init {
        val builder = ModConfigSpec.Builder()

        autoPlateRepeat = builder.defineInRange("autoPlateRepeat", 1, 1, 64)
        modeEncodingTransfer = builder.defineEnum("modeEncodingTransfer", ModeEncodingTransfer.MERGE_ADJACENCY)

        SPEC = builder.build()
    }

    fun init(modContainer: ModContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, SPEC, "extendedae_plus/client.toml")
        modContainer.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory(::ConfigurationScreen)
        )
    }

    private fun ModConfigSpec.Builder.section(section: String, modifier: Consumer<ModConfigSpec.Builder>): ModConfigSpec.Builder {
        this.push(section)
        modifier.accept(this)
        this.pop()
        return this
    }
}
