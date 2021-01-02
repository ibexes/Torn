package com.torn.assistant.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrganisedCrimeDTO implements Serializable {
    private List<UserDTO> participants;
    private Date plannedAt;
    private UserDTO plannedBy;
    private Date initiatedAt;
    private UserDTO initiatedBy;
    private Boolean success;
    private Long moneyGained;
    private Long respectGained;

    public OrganisedCrimeDTO() {
        participants = new ArrayList<>();
        success = false;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDTO> participants) {
        this.participants = participants;
    }

    public Date getPlannedAt() {
        return plannedAt;
    }

    public void setPlannedAt(Date plannedAt) {
        this.plannedAt = plannedAt;
    }

    public UserDTO getPlannedBy() {
        return plannedBy;
    }

    public void setPlannedBy(UserDTO plannedBy) {
        this.plannedBy = plannedBy;
    }

    public Date getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(Date initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public UserDTO getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(UserDTO initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getMoneyGained() {
        return moneyGained;
    }

    public void setMoneyGained(Long moneyGained) {
        this.moneyGained = moneyGained;
    }

    public Long getRespectGained() {
        return respectGained;
    }

    public void setRespectGained(Long respectGained) {
        this.respectGained = respectGained;
    }
}
