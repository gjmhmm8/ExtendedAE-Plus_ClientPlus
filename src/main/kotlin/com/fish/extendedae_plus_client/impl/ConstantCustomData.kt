package com.fish.extendedae_plus_client.impl

/** 不要批斗我😭, 不能注册component你来你也过不了第二关( */
enum class ConstantCustomData {
    autoCompletable,
    ;

    private val nameConstant: String

    constructor(name: String) {
        this.nameConstant = name
    }

    constructor() {
        this.nameConstant = this.name
    }

    fun get(): String {
        return "eaepc:$nameConstant"
    }
}
