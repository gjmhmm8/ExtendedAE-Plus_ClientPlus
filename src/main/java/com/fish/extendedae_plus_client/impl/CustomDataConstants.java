package com.fish.extendedae_plus_client.impl;

public enum CustomDataConstants {
    autoCompletable,

    ;

    private final String name;

    CustomDataConstants(String name) {
        this.name = "eaepc:" + name;
    }

    CustomDataConstants() {
        this.name = "eaepc:" + this;
    }

    public String get() {
        return name;
    }
}
