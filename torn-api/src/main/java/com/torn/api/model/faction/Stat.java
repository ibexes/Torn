package com.torn.api.model.faction;

public enum Stat {
    STRENGTH("gymstrength"),
    SPEED("gymspeed"),
    DEXTERITY("gymdexterity"),
    DEFENCE("gymdefense");

    private String value;
    Stat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}