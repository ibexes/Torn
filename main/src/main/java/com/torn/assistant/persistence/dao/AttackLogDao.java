package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.AttackLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttackLogDao extends JpaRepository<AttackLog, Long> {
    Optional<AttackLog> findByLog(String log);
}
