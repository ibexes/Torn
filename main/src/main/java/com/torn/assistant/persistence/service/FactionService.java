package com.torn.assistant.persistence.service;

import com.torn.assistant.authentication.LoginCredentials;
import com.torn.assistant.persistence.dao.FactionDao;
import com.torn.assistant.persistence.dao.UserDao;
import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.torn.assistant.utils.UserUtils.getUserId;

@Service
public class FactionService {
    private final FactionDao factionDao;
    private final UserDao userDao;

    public FactionService(FactionDao factionDao, UserDao userDao) {
        this.factionDao = factionDao;
        this.userDao = userDao;
    }

    public Faction getFaction(String username) {
        Long userId = getUserId(username);
        if (userId == null) {
            LoginCredentials loginCredentials = LoginCredentials.getUser(username);
            return loginCredentials == null ? null : factionDao.findById(loginCredentials.getFactionId()).orElse(null);
        } else {
            User user = userDao.findByUserId(userId).orElseThrow(() -> new RuntimeException("No user found"));
            return user.getFaction();
        }
    }

    public List<User> getMembers(Long factionId) {
        return userDao.findByFactionEquals(factionDao.findById(factionId).orElse(null));
    }

    public Faction getFaction(Long factionId) {
        return factionDao.findById(factionId).orElse(null);
    }
}
