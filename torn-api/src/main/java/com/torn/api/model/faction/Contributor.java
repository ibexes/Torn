package com.torn.api.model.faction;

import java.io.Serializable;
import java.util.Objects;

public class Contributor implements Serializable {
    private Member member;
    private Long contribution;

    public Contributor(Member member, Long contribution) {
        this.member = member;
        this.contribution = contribution;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setContribution(Long contribution) {
        this.contribution = contribution;
    }

    public Long getContribution() {
        return contribution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contributor that = (Contributor) o;
        return Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member);
    }
}
