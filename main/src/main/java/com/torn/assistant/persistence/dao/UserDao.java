package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
    List<User> findByFactionEquals(Faction faction);
}
