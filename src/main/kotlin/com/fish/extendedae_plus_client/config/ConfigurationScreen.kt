package com.fish.extendedae_plus_client.config

import com.fish.extendedae_plus_client.ExtendedAEPlusClient
import com.fish.extendedae_plus_client.config.enums.AutoUploadMode
import com.fish.extendedae_plus_client.config.enums.ModeEncodingTransfer
import com.fish.extendedae_plus_client.config.enums.TiggerMode
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object ConfigurationScreen {
    private const val CFG_KEY_PREFIX = "${ExtendedAEPlusClient.MODID}.configuration"

    private fun key(path: String): Component = Component.translatable("$CFG_KEY_PREFIX.$path")

    private fun enumKey(entryPath: String, enumName: String): Component =
        Component.translatable("$CFG_KEY_PREFIX.$entryPath.$enumName")

    fun create(parent: Screen?): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(key("title"))

        val entryBuilder = builder.entryBuilder()
        val category = builder.getOrCreateCategory(key("category.client"))

        category.addEntry(
            entryBuilder.startIntField(key("autoPlateRepeat"), EAEPCConfig.autoPlateRepeat.get())
                .setDefaultValue(1)
                .setMin(1)
                .setMax(64)
                .setTooltip(key("autoPlateRepeat.tooltip"))
                .setSaveConsumer { EAEPCConfig.autoPlateRepeat.set(it) }
                .build()
        )

        category.addEntry(
            entryBuilder.startIntField(key("autoTransferDelay"), EAEPCConfig.autoTransferDelay.get())
                .setDefaultValue(10)
                .setMin(1)
                .setMax(40)
                .setTooltip(key("autoTransferDelay.tooltip"))
                .setSaveConsumer { EAEPCConfig.autoTransferDelay.set(it) }
                .build()
        )

        category.addEntry(
            entryBuilder.startEnumSelector(
                key("modeEncodingTransfer"),
                ModeEncodingTransfer::class.java,
                EAEPCConfig.modeEncodingTransfer.get()
            )
                .setDefaultValue(ModeEncodingTransfer.MERGE_ADJACENCY)
                .setTooltip(key("modeEncodingTransfer.tooltip"))
                .setEnumNameProvider { enumKey("modeEncodingTransfer", it.name) }
                .setSaveConsumer { EAEPCConfig.modeEncodingTransfer.set(it) }
                .build()
        )

        category.addEntry(
            entryBuilder.startEnumSelector(
                key("autoUploadMode"),
                AutoUploadMode::class.java,
                EAEPCConfig.autoUploadMode.get()
            )
                .setDefaultValue(AutoUploadMode.AUTO_OPEN)
                .setTooltip(key("autoUploadMode.tooltip"))
                .setEnumNameProvider { enumKey("autoUploadMode", it.name) }
                .setSaveConsumer { EAEPCConfig.autoUploadMode.set(it) }
                .build()
        )

        category.addEntry(
            entryBuilder.startEnumSelector(
                key("encodingTiggerMode"),
                TiggerMode::class.java,
                EAEPCConfig.encodingTiggerMode.get()
            )
                .setDefaultValue(TiggerMode.ON_NOT_SHIFT)
                .setTooltip(key("encodingTiggerMode.tooltip"))
                .setEnumNameProvider { enumKey("tiggerMode", it.name) }
                .setSaveConsumer { EAEPCConfig.encodingTiggerMode.set(it) }
                .build()
        )

        category.addEntry(
            entryBuilder.startEnumSelector(
                key("itemEditingTiggerMode"),
                TiggerMode::class.java,
                EAEPCConfig.itemEditingTiggerMode.get()
            )
                .setDefaultValue(TiggerMode.ON_CTRL)
                .setTooltip(key("itemEditingTiggerMode.tooltip"))
                .setEnumNameProvider { enumKey("tiggerMode", it.name) }
                .setSaveConsumer { EAEPCConfig.itemEditingTiggerMode.set(it) }
                .build()
        )

        category.addEntry(
            entryBuilder.startEnumSelector(
                key("autoEncodingTiggerMode"),
                TiggerMode::class.java,
                EAEPCConfig.autoEncodingTiggerMode.get()
            )
                .setDefaultValue(TiggerMode.ON_CTRL)
                .setTooltip(key("autoEncodingTiggerMode.tooltip"))
                .setEnumNameProvider { enumKey("tiggerMode", it.name) }
                .setSaveConsumer { EAEPCConfig.autoEncodingTiggerMode.set(it) }
                .build()
        )

        builder.setSavingRunnable { EAEPCConfig.SPEC.save() }
        return builder.build()
    }
}

