package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Member;
import com.torn.assistant.persistence.dao.FactionDao;
import com.torn.assistant.persistence.dao.UserDao;
import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.Settings;
import com.torn.assistant.persistence.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.torn.api.client.FactionApiClient.getMembers;

@Service
public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
    private final FactionDao factionDao;
    private final UserDao userDao;
    private final PlayerTrackerService playerTrackerService;
    private final FactionStatsService factionStatsService;
    private final TornApiService tornApiService;
    private final ExecutorService es;

    public SchedulerService(FactionDao factionDao, UserDao userDao, PlayerTrackerService playerTrackerService,
                            FactionStatsService factionStatsService, TornApiService tornApiService,
                            @Value("${activity.threads:4}") int threads) {
        this.factionDao = factionDao;
        this.userDao = userDao;
        this.playerTrackerService = playerTrackerService;
        this.factionStatsService = factionStatsService;
        this.tornApiService = tornApiService;
        this.es = Executors.newFixedThreadPool(threads);
    }

    @Scheduled(cron = "${STATS_CRON:0 */5 * * * ?}")
    @Transactional
    public void processFactionStats()  {
        factionStatsService.run(false);
    }

    @Scheduled(cron = "${ACTIVITY_CRON:0 */5 * * * ?}")
    @Transactional
    public void processFactionActivity() throws InterruptedException {
        AtomicInteger count = new AtomicInteger();
        Settings settings = tornApiService.getSettings();
        List<Faction> factions = factionDao.findByTrackActivityTrue();

        List<Callable<Object>> todo = new ArrayList<>(factions.size());

        if (settings.getApiKeys().isEmpty()) {
            logger.warn("There are no api keys in settings, skipping activity tracking");
            return;
        }

        for (Faction faction : factions) {
            todo.add(Executors.callable(() -> {
                logger.info("Tracking player activity for {}", faction.getName());
                try {
                    Date now = new Date();
                    List<Member> memberList = getMembers(tornApiService.getApiKey(settings), faction.getId());
                    for (Member member : memberList) {
                        try {
                            playerTrackerService.trackActivity(member.getUserId(), member.getName(), member.getLastAction(), now);
                            playerTrackerService.updateFaction(member.getUserId(), member.getName(), member.getFactionId());
                            count.getAndIncrement();
                        } catch (DataIntegrityViolationException ignored) {

                        } catch (Exception e) {
                            logger.error("error processing user {}", member.getName(), e);
                        }
                    }
                } catch (JsonProcessingException | TornApiAccessException | InterruptedException e) {
                    logger.error("error processing faction {}", faction.getName(), e);
                }
            }));
        }

        es.invokeAll(todo);

        logger.info("Processed {} people activity this iteration", count.get());
    }

    @Scheduled(cron = "${TRACKER_CRON:30 0 * * * ?}")
    @Transactional
    public void processFactions() throws InterruptedException {
        int count = 0;
        Settings settings = tornApiService.getSettings();
        for (Faction faction : factionDao.findByTrackStatsTrue()) {
            logger.info("Tracking player stats for {}", faction.getName());
            if (settings.getApiKeys().isEmpty()) {
                logger.warn("There are no api keys in settings");
                continue;
            }

            try {
                List<Member> memberList = getMembers(tornApiService.getApiKey(settings), faction.getId());
                for (Member member : memberList) {
                    try {
                        // skip them if their last action was the same or before the one we got
                        Optional<User> user = userDao.findByUserId(member.getUserId());
                        if(user.isPresent()) {
                            if (user.get().getLastAction() != null &&
                                    member.getLastAction().compareTo(user.get().getLastAction()) == 0) {
                                continue;
                            }
                        }
                        playerTrackerService.processUser(member.getUserId(), tornApiService.getApiKey(settings));
                        count++;
                    } catch (DataIntegrityViolationException ignored) {

                    } catch (Exception e) {
                        logger.error("error processing user {}", member.getName(), e);
                    }
                }
            } catch (JsonProcessingException | TornApiAccessException e) {
                logger.error("error processing faction {}", faction.getName(), e);
            }
        }
        logger.info("Processed {} people personal stats this iteration", count);
    }
}
