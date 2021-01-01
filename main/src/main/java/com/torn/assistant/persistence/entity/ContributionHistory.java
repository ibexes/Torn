package com.torn.assistant.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class ContributionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime fetchedAt;

    @OneToOne
    private Faction faction;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserContribution> userActivities;

    public ContributionHistory() {
    }

    public ContributionHistory(Faction faction) {
        this.faction = faction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public List<UserContribution> getUserActivities() {
        return userActivities;
    }

    public void setUserActivities(List<UserContribution> userActivities) {
        this.userActivities = userActivities;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
}
