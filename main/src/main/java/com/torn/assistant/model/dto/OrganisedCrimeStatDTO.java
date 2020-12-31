package com.torn.assistant.model.dto;

import java.io.Serializable;

public class OrganisedCrimeStatDTO implements Serializable {
    private String name;
    private Integer successes = 0;
    private Integer attempts = 0;

    public OrganisedCrimeStatDTO(String name) {
        this.name = name;
    }

    public Integer getSuccesses() {
        return successes;
    }

    public void setSuccesses(Integer successes) {
        this.successes = successes;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
