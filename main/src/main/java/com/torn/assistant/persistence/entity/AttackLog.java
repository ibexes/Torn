package com.torn.assistant.persistence.entity;

import com.torn.api.model.faction.AttackType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

@Entity
public class AttackLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long attackerId;

    private Long attackerFaction;

    @Column(nullable = false)
    private Long defenderId;

    private Long defenderFaction;

    @Column(nullable = false)
    private AttackType attackType;

    private Boolean stealth;

    @Column(unique = true, nullable = false)
    private String log;
    private Date initiated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttackerId() {
        return attackerId;
    }

    public void setAttackerId(Long attackerId) {
        this.attackerId = attackerId;
    }

    public Long getAttackerFaction() {
        return attackerFaction;
    }

    public void setAttackerFaction(Long attackerFaction) {
        this.attackerFaction = attackerFaction;
    }

    public Long getDefenderId() {
        return defenderId;
    }

    public void setDefenderId(Long defenderId) {
        this.defenderId = defenderId;
    }

    public Long getDefenderFaction() {
        return defenderFaction;
    }

    public void setDefenderFaction(Long defenderFaction) {
        this.defenderFaction = defenderFaction;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
    }

    public Boolean getStealth() {
        return stealth;
    }

    public void setStealth(Boolean stealth) {
        this.stealth = stealth;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Date getInitiated() {
        return initiated;
    }

    public void setInitiated(Date initiated) {
        this.initiated = initiated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttackLog attackLog = (AttackLog) o;
        return Objects.equals(log, attackLog.log);
    }

    @Override
    public int hashCode() {
        return Objects.hash(log);
    }
}
