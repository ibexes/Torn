package com.torn.api.model.faction;

public enum AttackType {
    ATTACKED("Attacked", "attacked"),
    MUGGED("Mugged", "mugged"),
    ASSIST("Assist", "assisted"),
    LOST("Lost", "lost to"),
    HOSPITALIZED("Hospitalized", "hospitalized"),
    ESCAPE("Escape", "escaped from"),
    SPECIAL("Special", "used a special on"),
    UNKNOWN("", "unknown");

    private final String type;
    private final String readable;

    AttackType(String type, String readable) {
        this.type = type;
        this.readable = readable;
    }

    public String getType() {
        return type;
    }

    public static AttackType convertToAttackType(String type) {
        for(AttackType attackType : AttackType.values()) {
            if(attackType.getType().equals(type)) {
                return attackType;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return this.readable;
    }
}
