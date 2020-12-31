package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.OrganisedCrime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrganisedCrimeDao extends JpaRepository<OrganisedCrime, Long> {
    Optional<OrganisedCrime> findTopByFactionEqualsOrderByPlannedAtAsc(Faction faction);
    List<OrganisedCrime> findByFactionEqualsAndPlannedAtBetween(Faction faction, Date start, Date end);
}
