package com.torn.assistant.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@Table(name = "USER_STATS", uniqueConstraints={@UniqueConstraint(columnNames={"USER_ID", "USER_ACTIVITY"})})
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="USER_ID", nullable=false)
    private User user;

    private Date timestamp;

    private Long attacks;

    private Long xanax;

    private Long energyRefill;

    private Long totalCrimes;

    @ManyToOne
    @JoinColumn(name = "USER_ACTIVITY", nullable = false)
    private UserActivity userActivity;

    public UserStats() {
    }

    public UserStats(User user) {
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAttacks() {
        return attacks;
    }

    public void setAttacks(Long attacks) {
        this.attacks = attacks;
    }

    public Long getXanax() {
        return xanax;
    }

    public void setXanax(Long xanax) {
        this.xanax = xanax;
    }

    public Long getEnergyRefill() {
        return energyRefill;
    }

    public void setEnergyRefill(Long energyRefill) {
        this.energyRefill = energyRefill;
    }

    public UserActivity getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(UserActivity userActivity) {
        this.userActivity = userActivity;
    }

    public Long getTotalCrimes() {
        return totalCrimes;
    }

    public void setTotalCrimes(Long totalCrimes) {
        this.totalCrimes = totalCrimes;
    }
}
