package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsDao extends JpaRepository<Settings, Long> {
}
