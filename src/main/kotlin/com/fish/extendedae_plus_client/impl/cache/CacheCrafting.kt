package com.fish.extendedae_plus_client.impl.cache

import kotlin.math.max

object CacheCrafting {
    private var markedPlan = 0

    @JvmStatic
    var isOpening: Boolean = false
        get() {
            if (field) {
                field = false
                return true
            } else return false
        }

    @JvmStatic
    val isEmpty: Boolean
        get() = markedPlan == 0

    @JvmStatic
    fun markPlan() {
        markedPlan++
    }

    @JvmStatic
    fun cancelPlan() {
        markedPlan = max(markedPlan - 1, 0)
    }

    fun clear() {
        this.isOpening = false
        this.markedPlan = 0
    }
}
