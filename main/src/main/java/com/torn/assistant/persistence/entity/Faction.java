package com.torn.assistant.persistence.entity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Faction {

    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    private String name;

    private Boolean trackContributions;

    private Boolean trackOrganisedCrimes;

    private Boolean trackAttacks;

    private Boolean trackStats;

    @ElementCollection
    private List<String> attacksWebhooks;

    private Date trackContributionsLastRun;

    @ElementCollection
    private Set<String> apiKey;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getTrackContributions() {
        return trackContributions;
    }

    public void setTrackContributions(Boolean trackContributions) {
        this.trackContributions = trackContributions;
    }

    public Set<String> getApiKey() {
        return apiKey;
    }

    public void setApiKey(Set<String> apiKey) {
        this.apiKey = apiKey;
    }

    public Boolean getTrackOrganisedCrimes() {
        return trackOrganisedCrimes;
    }

    public void setTrackOrganisedCrimes(Boolean trackOrganisedCrimes) {
        this.trackOrganisedCrimes = trackOrganisedCrimes;
    }

    public Date getTrackContributionsLastRun() {
        return trackContributionsLastRun;
    }

    public void setTrackContributionsLastRun(Date trackContributionsLastRun) {
        this.trackContributionsLastRun = trackContributionsLastRun;
    }

    public Boolean getTrackAttacks() {
        return trackAttacks;
    }

    public void setTrackAttacks(Boolean trackAttacks) {
        this.trackAttacks = trackAttacks;
    }

    public List<String> getAttacksWebhooks() {
        return attacksWebhooks;
    }

    public void setAttacksWebhooks(List<String> attacksWebhooks) {
        this.attacksWebhooks = attacksWebhooks;
    }

    public Boolean getTrackStats() {
        return trackStats;
    }

    public void setTrackStats(Boolean trackStats) {
        this.trackStats = trackStats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faction faction = (Faction) o;
        return Objects.equals(id, faction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
