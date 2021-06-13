package com.torn.api.model.faction;

public enum AttackType {
    ATTACKED("Attacked", "attacked", "was attacked by"),
    MUGGED("Mugged", "mugged", "was mugged by"),
    ASSIST("Assist", "assisted", "was assisted by"),
    LOST("Lost", "lost to", "got a defend from"),
    HOSPITALIZED("Hospitalized", "hospitalized", "was hospitalized by"),
    ESCAPE("Escape", "escaped from", "escaped from"),
    SPECIAL("Special", "used a special on", "received a special from"),
    STALEMATED("Stalemate", "stalemated with", "stalemated with"),
    ARRESTED("Arrested", "arrested", "was arrested by"),
    TIMEOUT("Timeout", "timed out against", "got a defend (timeout) from"),
    UNKNOWN("", "unknown", "unknown");

    private final String type;
    private final String readable;
    private final String inverted;

    AttackType(String type, String readable, String inverted) {
        this.type = type;
        this.readable = readable;
        this.inverted = inverted;
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

    public String getInverted() {
        return this.inverted;
    }
}
