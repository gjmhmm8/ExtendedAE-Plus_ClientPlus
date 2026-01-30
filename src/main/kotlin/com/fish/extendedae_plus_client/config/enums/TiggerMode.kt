package com.fish.extendedae_plus_client.config.enums

import net.minecraft.client.gui.screens.Screen

enum class TiggerMode {
    ON_SHIFT,
    ON_NOT_SHIFT,
    ON_CTRL,
    ON_NOT_CTRL,
    ON_ALT,
    ON_NOT_ALT,
    NONE;

    fun shouldTigger(): Boolean{
        return when (this){
            ON_SHIFT -> Screen.hasShiftDown()
            ON_NOT_SHIFT -> !Screen.hasShiftDown()
            ON_CTRL -> Screen.hasControlDown()
            ON_NOT_CTRL -> !Screen.hasControlDown()
            ON_ALT -> Screen.hasAltDown()
            ON_NOT_ALT -> !Screen.hasAltDown()
            NONE -> false
        }
    }
}