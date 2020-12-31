package com.torn.api.model.faction;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Member implements Serializable {
    private Long userId;
    private String name;
    private Date lastAction;
    private Long factionId = -1L; // set default so we can use for comparisons without NPE

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

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }

    public Long getFactionId() {
        return factionId;
    }

    public void setFactionId(Long factionId) {
        this.factionId = factionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(userId, member.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
