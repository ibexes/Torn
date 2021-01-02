package com.torn.api.model.faction;

import java.util.Date;
import java.util.List;

public class OrganisedCrime {
    private Long id;
    private OrganisedCrimeType crimeType;
    private List<Long> participants;
    private Long moneyGained;
    private Long respectGained;
    private Boolean initiated;
    private Boolean success;
    private Long plannedBy;
    private Long initiatedBy;
    private Date plannedAt;
    private Date readyAt;
    private Date initiatedAt;

    public OrganisedCrime(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrganisedCrimeType getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(OrganisedCrimeType crimeType) {
        this.crimeType = crimeType;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
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

    public Boolean getInitiated() {
        return initiated;
    }

    public void setInitiated(Boolean initiated) {
        this.initiated = initiated;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getPlannedBy() {
        return plannedBy;
    }

    public void setPlannedBy(Long plannedBy) {
        this.plannedBy = plannedBy;
    }

    public Long getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(Long initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public Date getPlannedAt() {
        return plannedAt;
    }

    public void setPlannedAt(Date plannedAt) {
        this.plannedAt = plannedAt;
    }

    public Date getReadyAt() {
        return readyAt;
    }

    public void setReadyAt(Date readyAt) {
        this.readyAt = readyAt;
    }

    public Date getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(Date initiatedAt) {
        this.initiatedAt = initiatedAt;
    }
}
