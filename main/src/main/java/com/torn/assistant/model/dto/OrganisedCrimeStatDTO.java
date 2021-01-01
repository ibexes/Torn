package com.torn.assistant.model.dto;

import java.io.Serializable;

public class OrganisedCrimeStatDTO implements Serializable {
    private String name;
    private Integer successes = 0;
    private Integer attempts = 0;
    private Long profit = 0L;
    private Long respect = 0L;

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

    public Long getProfit() {
        return profit;
    }

    public void setProfit(Long profit) {
        this.profit = profit;
    }

    public Long getRespect() {
        return respect;
    }

    public void setRespect(Long respect) {
        this.respect = respect;
    }
}
