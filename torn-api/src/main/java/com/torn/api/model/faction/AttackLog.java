package com.torn.api.model.faction;

import java.util.Date;

public class AttackLog {
    // attack log id
    private String log;
    private String attacker;
    private Long attackerId;
    private Long attackerFaction;
    private String attackerFactionName;
    private String defender;
    private Long defenderId;
    private Long defenderFaction;
    private String defenderFactionName;
    private AttackType attackType;
    private Boolean stealth;
    private Date initiated;
    private Double respect;
    private Double fairFight;
    private Integer chain;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
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

    public Date getInitiated() {
        return initiated;
    }

    public void setInitiated(Date initiated) {
        this.initiated = initiated;
    }

    public Double getRespect() {
        return respect;
    }

    public void setRespect(Double respect) {
        this.respect = respect;
    }

    public String getAttacker() {
        return attacker;
    }

    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public String getDefender() {
        return defender;
    }

    public void setDefender(String defender) {
        this.defender = defender;
    }

    public String getAttackerFactionName() {
        return attackerFactionName;
    }

    public void setAttackerFactionName(String attackerFactionName) {
        this.attackerFactionName = attackerFactionName;
    }

    public String getDefenderFactionName() {
        return defenderFactionName;
    }

    public void setDefenderFactionName(String defenderFactionName) {
        this.defenderFactionName = defenderFactionName;
    }

    public Double getFairFight() {
        return fairFight;
    }

    public void setFairFight(Double fairFight) {
        this.fairFight = fairFight;
    }

    public Integer getChain() {
        return chain;
    }

    public void setChain(Integer chain) {
        this.chain = chain;
    }
}
