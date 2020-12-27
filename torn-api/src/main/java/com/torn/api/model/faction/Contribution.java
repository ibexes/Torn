package com.torn.api.model.faction;

import java.util.*;

public class Contribution {
    private Stat stat;
    private Set<Contributor> contributors;

    public Contribution(Stat stat) {
        this.stat = stat;
        this.contributors = new HashSet<>();
    }

    public void addContributor(Contributor contributor) {
        contributors.add(contributor);
    }

    public Stat getStat() {
        return stat;
    }

    public Set<Contributor> getContributors() {
        return contributors;
    }

    @Override
    public String toString() {
        return "Contribution{" +
                "stat=" + stat +
                ", contributors=" + contributors +
                '}';
    }
}
