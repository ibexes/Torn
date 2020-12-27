package com.torn.assistant.model.dto;

import java.io.Serializable;
import java.util.Date;

public class UserContributionDTO implements Serializable {
    private Long userId;
    private String name;
    private Long gymSpeed;
    private Long gymDexterity;
    private Long gymDefence;
    private Long gymStrength;

    public UserContributionDTO(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGymSpeed() {
        return gymSpeed;
    }

    public void setGymSpeed(Long gymSpeed) {
        this.gymSpeed = gymSpeed;
    }

    public Long getGymDexterity() {
        return gymDexterity;
    }

    public void setGymDexterity(Long gymDexterity) {
        this.gymDexterity = gymDexterity;
    }

    public Long getGymDefence() {
        return gymDefence;
    }

    public void setGymDefence(Long gymDefence) {
        this.gymDefence = gymDefence;
    }

    public Long getGymStrength() {
        return gymStrength;
    }

    public void setGymStrength(Long gymStrength) {
        this.gymStrength = gymStrength;
    }

}
