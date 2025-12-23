package com.fish.extendedae_plus_client.impl;

/// 不要批斗我😭, 不能注册component你来你也过不了第二关(
public enum ConstantCustomData {
    autoCompletable,

    ;

    private final String name;

    ConstantCustomData(String name) {
        this.name = name;
    }

    ConstantCustomData() {
        this.name = this.name();
    }

    public String get() {
        return "eaepc:" + name;
    }
}
