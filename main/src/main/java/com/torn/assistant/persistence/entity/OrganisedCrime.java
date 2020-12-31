package com.torn.assistant.persistence.entity;

import com.torn.api.model.faction.OrganisedCrimeType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.Set;

@Entity
public class OrganisedCrime {
    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> participants;

    @Column(nullable = false)
    private OrganisedCrimeType crimeType;
    private Date plannedAt;
    private Long plannedBy;
    private Date readyAt;
    private Boolean initiated;
    private Date initiatedAt;
    private Long initiatedBy;
    private Boolean success;
    private Long moneyGained;
    private Long respectGained;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public OrganisedCrimeType getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(OrganisedCrimeType crimeType) {
        this.crimeType = crimeType;
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