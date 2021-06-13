package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatsDao extends JpaRepository<UserStats, Long> {
}
