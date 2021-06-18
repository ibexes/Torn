package com.torn.api.model.faction;

import java.io.Serializable;
import java.util.Date;

public class Player implements Serializable {
    private Long userId;
    private String name;
    private Date lastAction;
    private Long factionId = -1L;
    private Long energyRefill;
    private Long xanax;
    private Long level;
    private Long attacks;
    private Long totalCrimes;
    private Date timestamp;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }

    public Long getFactionId() {
        return factionId;
    }

    public void setFactionId(Long factionId) {
        this.factionId = factionId;
    }

    public Long getXanax() {
        return xanax;
    }

    public void setXanax(Long xanax) {
        this.xanax = xanax;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getEnergyRefill() {
        return energyRefill;
    }

    public void setEnergyRefill(Long energyRefill) {
        this.energyRefill = energyRefill;
    }

    public Long getAttacks() {
        return attacks;
    }

    public void setAttacks(Long attacks) {
        this.attacks = attacks;
    }

    public Long getTotalCrimes() {
        return totalCrimes;
    }

    public void setTotalCrimes(Long totalCrimes) {
        this.totalCrimes = totalCrimes;
    }
}
