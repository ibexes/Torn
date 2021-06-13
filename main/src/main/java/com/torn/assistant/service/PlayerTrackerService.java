package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Player;
import com.torn.assistant.persistence.dao.UserDao;
import com.torn.assistant.persistence.dao.UserStatsDao;
import com.torn.assistant.persistence.entity.User;
import com.torn.assistant.persistence.entity.UserStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.torn.api.client.UserApiClient.getPlayerDetails;

@Service
public class PlayerTrackerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerTrackerService.class);
    private final UserDao userDao;
    private final UserStatsDao userStatsDao;

    public PlayerTrackerService(UserDao userDao, UserStatsDao userStatsDao) {
        this.userDao = userDao;
        this.userStatsDao = userStatsDao;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processUser(Long userId, String apiKey) throws TornApiAccessException, JsonProcessingException {
        Player player = getPlayerDetails(apiKey, userId);

        User user = userDao.findByUserId(userId).orElse(new User(userId, player.getName()));
        user.setName(player.getName());
        user = userDao.save(user);

        UserStats userStats = new UserStats(user);
        userStats.setXanax(player.getXanax());
        userStats.setEnergyRefill(player.getEnergyRefill());
        userStats.setTimestamp(player.getTimestamp());
        userStats.setLastAction(player.getLastAction());
        userStats.setAttacks(player.getAttacks());

        userStatsDao.save(userStats);
    }
}
