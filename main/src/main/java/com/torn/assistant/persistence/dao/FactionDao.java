package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.Faction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FactionDao extends JpaRepository<Faction, Long> {
    List<Faction> findByTrackContributionsIsTrueAndTrackContributionsLastRunBeforeOrTrackContributionsIsTrueAndTrackContributionsLastRunIsNull(Date date);
    List<Faction> findByTrackContributionsIsTrue();
    List<Faction> findByTrackOrganisedCrimesIsTrue();
    List<Faction> findByTrackAttacksTrue();
    List<Faction> findByTrackStatsTrue();
}
