package com.fish.extendedae_plus_client.impl.cache

import appeng.api.crafting.IPatternDetails
import appeng.client.gui.me.patternaccess.PatternContainerRecord

object CacheProvider {
    @JvmStatic
    val providerList: MutableMap<Int, PatternContainerRecord> = HashMap()
    @JvmStatic
    private val selectedProvider: MutableMap<IPatternDetails, Int> = HashMap()

    @JvmStatic
    fun markPattern(pattern: IPatternDetails, hashGroup: Int) {
        selectedProvider[pattern] = hashGroup
    }

    @JvmStatic
    fun clearPattern() {
        selectedProvider.clear()
    }

    @JvmStatic
    fun findProvider(pattern: IPatternDetails?): Int? {
        return selectedProvider[pattern]
    }

    @JvmStatic
    fun putProvider(record: PatternContainerRecord) {
        providerList[record.group.hashCode()] = record
    }

    @JvmStatic
    fun clearProvider() {
        providerList.clear()
    }
}
