package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Player;
import com.torn.assistant.model.dto.UserActivityDTO;
import com.torn.assistant.persistence.dao.UserActivityDao;
import com.torn.assistant.persistence.dao.UserDao;
import com.torn.assistant.persistence.dao.UserStatsDao;
import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.User;
import com.torn.assistant.persistence.entity.UserActivity;
import com.torn.assistant.persistence.entity.UserStats;
import com.torn.assistant.persistence.service.FactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.torn.api.client.UserApiClient.getPlayerDetails;

@Service
public class PlayerTrackerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerTrackerService.class);
    private final UserDao userDao;
    private final UserStatsDao userStatsDao;
    private final UserActivityDao userActivityDao;
    private final FactionService factionService;

    public PlayerTrackerService(UserDao userDao, UserStatsDao userStatsDao, UserActivityDao userActivityDao, FactionService factionService) {
        this.userDao = userDao;
        this.userStatsDao = userStatsDao;
        this.userActivityDao = userActivityDao;
        this.factionService = factionService;
    }

    public List<Long> getFactionMembers(Long factionId) {
        List<User> user = factionService.getMembers(factionId);
        return user.stream().map(User::getUserId).collect(Collectors.toList());
    }

    public List<UserActivityDTO> getUserActivity(Long userId) {
        Map<LocalDate, UserActivityDTO> localDateUserActivityDTOMap = new HashMap<>();

        Optional<User> user = userDao.findByUserId(userId);
        if (user.isPresent()) {
            List<UserActivity> userActivities = userActivityDao.findAllByUserOrderByLastAction(user.get());

            for (UserActivity userActivity : userActivities) {
                LocalDate date = userActivity.getLastAction().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();

                UserActivityDTO userActivityDTO = localDateUserActivityDTOMap.getOrDefault(date, new UserActivityDTO());
                userActivityDTO.setName(user.get().getName());
                userActivityDTO.setDate(Date.from(date.atStartOfDay().atZone(ZoneId.of("UTC")).toInstant()));
                userActivityDTO.getActive().add(nearestMinuteMultiple(5, userActivity.getLastAction()));
                localDateUserActivityDTOMap.put(date, userActivityDTO);
            }
        }

        return localDateUserActivityDTOMap.values().stream().sorted(Comparator.comparing(UserActivityDTO::getDate))
                .collect(Collectors.toList());
    }

    static Date nearestMinuteMultiple(int multiple, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % multiple;
        calendar.add(Calendar.MINUTE, unroundedMinutes == 0 ? -multiple : -mod);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processUser(Long userId, String apiKey) throws TornApiAccessException, JsonProcessingException {
        Player player = getPlayerDetails(apiKey, userId);

        User user = userDao.findByUserId(userId).orElse(new User(userId, player.getName()));
        user.setName(player.getName());
        user.setLastAction(player.getLastAction());
        user = userDao.save(user);

        UserStats userStats = new UserStats(user);
        userStats.setXanax(player.getXanax());
        userStats.setEnergyRefill(player.getEnergyRefill());
        userStats.setTimestamp(player.getTimestamp());
        userStats.setUserActivity(trackActivity(userId, player.getName(), player.getLastAction(), player.getTimestamp()));
        userStats.setAttacks(player.getAttacks());
        userStats.setTotalCrimes(player.getTotalCrimes());

        userStatsDao.save(userStats);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserActivity trackActivity(Long userId, String playerName, Date lastAction, Date timestamp) {
        Optional<User> userOptional = userDao.findByUserId(userId);

        User user = new User(userId, playerName);
        if (userOptional.isPresent()) {
           user = userOptional.get();
        } else {
            userDao.save(user);
        }

        UserActivity userActivity = userActivityDao.findByUserAndLastAction(user,lastAction).orElse(new UserActivity(user));
        if (userActivity.getLastAction() == null) {
            userActivity.setLastAction(lastAction);
            userActivity.setTimestamp(timestamp);
        }
        return userActivityDao.save(userActivity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateFaction(Long userId, String playerName, Long factionId) {
        Optional<User> userOptional = userDao.findByUserId(userId);

        User user = new User(userId, playerName);
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getFaction() == null) {
                Faction faction = factionService.getFaction(factionId);
                if (faction != null) {
                    user.setFaction(faction);
                    userDao.save(user);
                }
            }
        } else {
            user.setFaction(factionService.getFaction(factionId));
            userDao.save(user);
        }
    }
}
