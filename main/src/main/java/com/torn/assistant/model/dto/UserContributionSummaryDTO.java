package com.torn.assistant.model.dto;

import java.io.Serializable;
import java.util.Date;

public class UserContributionSummaryDTO implements Serializable {
    private Long userId;
    private String name;
    private StatDTO gymSpeed;
    private StatDTO gymDexterity;
    private StatDTO gymDefence;
    private StatDTO gymStrength;
    private StatDTO gymTotal;
    private Date lastAction;
    private Boolean inFaction = false;

    public UserContributionSummaryDTO(Long userId, String name) {
        this.userId = userId;
        this.name = name;
        this.gymSpeed = new StatDTO();
        this.gymDexterity = new StatDTO();
        this.gymDefence = new StatDTO();
        this.gymStrength = new StatDTO();
        this.gymTotal = new StatDTO();
    }

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

    public StatDTO getGymSpeed() {
        return gymSpeed;
    }

    public void setGymSpeed(StatDTO gymSpeed) {
        this.gymSpeed = gymSpeed;
    }

    public StatDTO getGymDexterity() {
        return gymDexterity;
    }

    public void setGymDexterity(StatDTO gymDexterity) {
        this.gymDexterity = gymDexterity;
    }

    public StatDTO getGymDefence() {
        return gymDefence;
    }

    public void setGymDefence(StatDTO gymDefence) {
        this.gymDefence = gymDefence;
    }

    public StatDTO getGymStrength() {
        return gymStrength;
    }

    public void setGymStrength(StatDTO gymStrength) {
        this.gymStrength = gymStrength;
    }

    public StatDTO getGymTotal() {
        return gymTotal;
    }

    public void setGymTotal(StatDTO gymTotal) {
        this.gymTotal = gymTotal;
    }

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }

    public Boolean getInFaction() {
        return inFaction;
    }

    public void setInFaction(Boolean inFaction) {
        this.inFaction = inFaction;
    }
}
