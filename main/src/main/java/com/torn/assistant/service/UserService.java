package com.torn.assistant.service;

import com.torn.api.model.faction.Member;
import com.torn.assistant.persistence.dao.FactionDao;
import com.torn.assistant.persistence.dao.UserDao;
import com.torn.assistant.persistence.entity.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {
    private final UserDao userDao;
    private final FactionDao factionDao;

    public UserService(UserDao userDao, FactionDao factionDao) {
        this.userDao = userDao;
        this.factionDao = factionDao;
    }

    public Optional<User> findByUserId(Long userId) {
        return userDao.findByUserId(userId);
    }

    @Transactional
    public User saveOrUpdate(Member member, String apiKey) {
        Optional<User> userOptional = userDao.findByUserId(member.getUserId());
        User user = userOptional.orElseGet(() -> new User(member.getUserId(), member.getName()));
        user.setApiKey(apiKey);
        user.setFaction(factionDao.findById(member.getFactionId()).orElse(null));
        return userDao.save(user);
    }
}
