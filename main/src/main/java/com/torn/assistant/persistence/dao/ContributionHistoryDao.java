package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.ContributionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ContributionHistoryDao extends JpaRepository<ContributionHistory, Long> {
    List<ContributionHistory> findByFetchedAtBetweenOrderByFetchedAtAsc(LocalDateTime start, LocalDateTime end);
}
