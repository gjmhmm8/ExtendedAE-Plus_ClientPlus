package com.fish.extendedae_plus_client.config

import com.fish.extendedae_plus_client.config.enums.AutoUploadMode
import com.fish.extendedae_plus_client.config.enums.ModeEncodingTransfer
import com.fish.extendedae_plus_client.config.enums.TiggerMode
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler

object EAEPCConfig {
    val SPEC: ForgeConfigSpec

    @JvmField
    val autoPlateRepeat: ForgeConfigSpec.IntValue
    @JvmField
    val autoTransferDelay: ForgeConfigSpec.IntValue
    @JvmField
    val modeEncodingTransfer: ForgeConfigSpec.EnumValue<ModeEncodingTransfer>

    @JvmField
    val autoUploadMode: ForgeConfigSpec.EnumValue<AutoUploadMode>
    @JvmField
    val encodingTiggerMode: ForgeConfigSpec.EnumValue<TiggerMode>
    @JvmField
    val itemEditingTiggerMode: ForgeConfigSpec.EnumValue<TiggerMode>
    @JvmField
    val autoEncodingTiggerMode: ForgeConfigSpec.EnumValue<TiggerMode>

    init {
        val builder = ForgeConfigSpec.Builder()

        autoPlateRepeat = builder.defineInRange("autoPlateRepeat", 1, 1, 64)
        autoTransferDelay = builder.defineInRange("autoTransferDelay", 10, 1, 40)
        modeEncodingTransfer = builder.defineEnum("modeEncodingTransfer", ModeEncodingTransfer.MERGE_ADJACENCY)
        autoUploadMode = builder.defineEnum("autoUploadMode", AutoUploadMode.AUTO_OPEN)
        encodingTiggerMode = builder.defineEnum("encodingTiggerMode", TiggerMode.ON_NOT_SHIFT)
        itemEditingTiggerMode = builder.defineEnum("itemEditingTiggerMode", TiggerMode.ON_CTRL)
        autoEncodingTiggerMode = builder.defineEnum("autoEncodingTiggerMode", TiggerMode.ON_CTRL)
        SPEC = builder.build()
    }

    fun init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC, "extendedae_plus/client.toml")
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory::class.java) {
                ConfigScreenHandler.ConfigScreenFactory { _, parent -> ConfigurationScreen.create(parent) }
            }
        }
    }
}
