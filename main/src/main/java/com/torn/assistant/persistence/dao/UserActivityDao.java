package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.User;
import com.torn.assistant.persistence.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserActivityDao extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findAllByUserOrderByLastAction(User user);
    Optional<UserActivity> findByUserAndLastAction(User user, Date lastAction);
}
