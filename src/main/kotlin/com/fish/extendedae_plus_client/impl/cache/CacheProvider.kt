package com.fish.extendedae_plus_client.impl.cache

import appeng.api.crafting.IPatternDetails
import appeng.api.implementations.blockentities.PatternContainerGroup
import appeng.client.gui.me.patternaccess.PatternContainerRecord

object CacheProvider {
    @JvmStatic
    var providerList: MutableMap<PatternContainerGroup,PatternContainerRecord> = HashMap()

    @JvmStatic
    private val selectedProvider: MutableMap<IPatternDetails, PatternContainerGroup> = HashMap()

    @JvmStatic
    fun markPattern(pattern: IPatternDetails, container: PatternContainerGroup) {
        selectedProvider[pattern] = container
    }

    @JvmStatic
    fun clearPattern() {
        selectedProvider.clear()
    }

    @JvmStatic
    fun findProvider(pattern: IPatternDetails?): PatternContainerGroup? {
        return selectedProvider[pattern]
    }

     @JvmStatic
    fun putProvider(container: PatternContainerRecord) {
        providerList[container.group]=container
    }

    @JvmStatic
    fun clearProvider() {
        providerList.clear()
    }
}
