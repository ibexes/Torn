package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.OrganisedCrime;
import com.torn.assistant.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrganisedCrimeDao extends JpaRepository<OrganisedCrime, Long> {
    List<OrganisedCrime> findByFaction(Faction faction);
    List<OrganisedCrime> findByFactionOrderByCrimeTypeDesc(Faction faction);
    Optional<OrganisedCrime> findTopByFactionEqualsOrderByPlannedAtAsc(Faction faction);
    List<OrganisedCrime> findByFactionEqualsAndPlannedAtBetween(Faction faction, Date start, Date end);
    List<OrganisedCrime> findByFactionEqualsAndParticipantsAndPlannedAtBetween(Faction faction, User user, Date plannedAt, Date plannedAt2);
}
