package com.torn.api.model.faction;

import java.io.Serializable;

public enum OrganisedCrimeType implements Serializable {
    BLACKMAIL(1, "Blackmail"),
    KIDNAPPING(2, "Kidnapping"),
    BOMB_THREAT(3, "Bomb threat"),
    PLANNED_ROBBERY(4, "Planned robbery"),
    MONEY_TRAIN(5, "Robbing of a money train"),
    CRUISE_LINER(6, "Take over a cruise liner"),
    PLANE_HIJACKING(7, "Planned robbery"),
    POLITICAL_ASSASSINATION(8, "Political Assassination");

    private String name;
    private int id;

    OrganisedCrimeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static OrganisedCrimeType convertToOrganisedCrimeType(int id) {
        for(OrganisedCrimeType organisedCrimeType : OrganisedCrimeType.values()) {
            if(organisedCrimeType.getId() == id) {
                return organisedCrimeType;
            }
        }
        throw new IllegalArgumentException("Id not found");
    }
}
