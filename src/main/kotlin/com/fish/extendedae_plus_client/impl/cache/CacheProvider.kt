package com.fish.extendedae_plus_client.impl.cache

import appeng.api.crafting.IPatternDetails
import appeng.api.implementations.blockentities.PatternContainerGroup
import appeng.client.gui.me.patternaccess.PatternContainerRecord

object CacheProvider {
    @JvmStatic
    private var providerList: MutableMap<PatternContainerGroup, MutableList<PatternContainerRecord>> = HashMap()

    @JvmStatic
    private val selectedProvider: MutableMap<IPatternDetails, PatternContainerGroup> = HashMap()

    @JvmStatic
    private val providerSlots: MutableMap<PatternContainerGroup, Int> = HashMap()

    @JvmStatic
    private val selectedPattern: MutableSet<IPatternDetails> = HashSet()

    @JvmStatic
    private val selectedPatternGroup: MutableMap<PatternContainerGroup, MutableSet<IPatternDetails>> = HashMap()

    @JvmStatic
    fun markPattern(pattern: IPatternDetails, container: PatternContainerGroup) {
        selectedProvider[pattern] = container
    }

    @JvmStatic
    fun unmarkPattern(pattern: IPatternDetails) {
        selectedProvider.remove(pattern)
    }

    @JvmStatic
    fun markPatternAlready(pattern: IPatternDetails,container: PatternContainerGroup) {
        selectedPattern.add(pattern);
        selectedPatternGroup.getOrPut(container, { HashSet() }).add(pattern);
    }

    @JvmStatic
    fun unmarkPatternAlready(pattern: IPatternDetails,container: PatternContainerGroup) {
        selectedPattern.remove(pattern)
        selectedPatternGroup.getOrDefault(container,HashSet()).remove(pattern);
    }

    @JvmStatic
    fun clearPattern() {
        selectedProvider.clear()
    }

    @JvmStatic
    fun clearPatternAlready() {
        selectedPattern.clear()
        selectedPatternGroup.clear()
    }

    @JvmStatic
    fun hasPatternAlready(pattern: IPatternDetails): Boolean {
        return selectedPattern.contains(pattern)
    }

    @JvmStatic
    fun findProvider(pattern: IPatternDetails?): PatternContainerGroup? {
        return selectedProvider[pattern]
    }

    @JvmStatic
    fun hasPattern(pattern: IPatternDetails): Boolean {
        return selectedPattern.contains(pattern) || selectedProvider.containsKey(pattern)
    }

    @JvmStatic
    fun putProvider(container: PatternContainerRecord, mabeHasSlot: Boolean) {
        if(mabeHasSlot){
            providerList.getOrPut(container.group) { mutableListOf() }.add(container)
        } else{
            providerList.getOrPut(container.group) { mutableListOf() }
        }
    }

    @JvmStatic
    fun addSlots(group:PatternContainerGroup, num:Int){
        providerSlots[group]=providerSlots.getOrDefault(group,0)+num
    }

    @JvmStatic
    fun getAvailableSlots(group:PatternContainerGroup): Int{
        val patternNum=selectedPatternGroup[group]?.size?:0
        return providerSlots.getOrDefault(group,0)-patternNum;
    }

    @JvmStatic
    fun getAvailableProvider(group:PatternContainerGroup ): PatternContainerRecord? {
        // 任意返回一个“未满”的：inventory 里存在空槽就认为可用
        val list = providerList[group] ?: return null
            for (rec in list) {
                val inv = rec.inventory
                val size = inv.size()
                for (i in 0 until size) {
                    if (inv.getStackInSlot(i).isEmpty) return rec
                }
            }

        return null;
    }

    @JvmStatic
    fun getGroups(): MutableSet<PatternContainerGroup> {
        return providerList.keys
    }

    @JvmStatic
    fun isEmpty(): Boolean{
        return providerList.isEmpty();
    }

    @JvmStatic
    fun clearProvider() {
        providerList.clear()
        providerSlots.clear()
    }
}