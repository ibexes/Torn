package com.torn.assistant.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
public class UserContribution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    private Long gymSpeed;
    private Long gymDexterity;
    private Long gymDefence;
    private Long gymStrength;
    private Date lastAction;

    public UserContribution() {
    }

    public UserContribution(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }
}
