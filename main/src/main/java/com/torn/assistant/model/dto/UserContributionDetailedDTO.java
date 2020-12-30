package com.torn.assistant.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserContributionDetailedDTO implements Serializable {
    private Long userId;
    private String name;

    private List<DataPointDTO> gymStrength;
    private List<DataPointDTO> gymDefence;
    private List<DataPointDTO> gymDexterity;
    private List<DataPointDTO> gymSpeed;
    private List<DataPointDTO> gymTotal;

    public UserContributionDetailedDTO() {
        gymStrength = new ArrayList<>();
        gymDefence = new ArrayList<>();
        gymDexterity = new ArrayList<>();
        gymSpeed = new ArrayList<>();
        gymTotal = new ArrayList<>();
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

    public List<DataPointDTO> getGymStrength() {
        return gymStrength;
    }

    public void setGymStrength(List<DataPointDTO> gymStrength) {
        this.gymStrength = gymStrength;
    }

    public List<DataPointDTO> getGymDefence() {
        return gymDefence;
    }

    public void setGymDefence(List<DataPointDTO> gymDefence) {
        this.gymDefence = gymDefence;
    }

    public List<DataPointDTO> getGymDexterity() {
        return gymDexterity;
    }

    public void setGymDexterity(List<DataPointDTO> gymDexterity) {
        this.gymDexterity = gymDexterity;
    }

    public List<DataPointDTO> getGymSpeed() {
        return gymSpeed;
    }

    public void setGymSpeed(List<DataPointDTO> gymSpeed) {
        this.gymSpeed = gymSpeed;
    }

    public List<DataPointDTO> getGymTotal() {
        return gymTotal;
    }

    public void setGymTotal(List<DataPointDTO> gymTotal) {
        this.gymTotal = gymTotal;
    }
}
