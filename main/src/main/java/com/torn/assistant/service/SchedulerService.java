package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Member;
import com.torn.assistant.persistence.dao.FactionDao;
import com.torn.assistant.persistence.dao.SettingsDao;
import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.torn.api.client.FactionApiClient.getMembers;

@Service
public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
    private static final Double USAGE_CAPACITY = 0.6; // use 60%
    private final SettingsDao settingsDao;
    private final FactionDao factionDao;
    private final PlayerTrackerService playerTrackerService;
    private final FactionStatsService factionStatsService;

    public SchedulerService(SettingsDao settingsDao, FactionDao factionDao, PlayerTrackerService playerTrackerService,
                            FactionStatsService factionStatsService) {
        this.settingsDao = settingsDao;
        this.factionDao = factionDao;
        this.playerTrackerService = playerTrackerService;
        this.factionStatsService = factionStatsService;
    }

    @Scheduled(cron = "${STATS_CRON:0 */5 * * * ?}")
    @Transactional
    public void processFactionStats()  {
        factionStatsService.run(false);
    }

    @Scheduled(cron = "${TRACKER_CRON:0 */15 * * * ?}")
    @Transactional
    public void processFactions() throws InterruptedException {
        Settings settings = getSettings();
        for (Faction faction : factionDao.findByTrackStatsTrue()) {
            logger.info("Tracking player stats for {}", faction.getName());
            if (settings.getApiKeys().isEmpty()) {
                logger.warn("There are no api keys in settings");
                continue;
            }

            try {
                List<Member> memberList = getMembers(getApiKey(settings), faction.getId());
                for (Member member : memberList) {
                    try {
                        playerTrackerService.processUser(member.getUserId(), getApiKey(settings));
                    } catch (DataIntegrityViolationException ignored) {

                    } catch (Exception e) {
                        logger.error("error processing user {}", member.getName(), e);
                    }
                }
            } catch (JsonProcessingException | TornApiAccessException e) {
                logger.error("error processing faction {}", faction.getName(), e);
            }
        }
        settingsDao.save(settings);
    }

    private String getApiKey(Settings settings) throws InterruptedException {
        settings.getStart();
        if (settings.getCount() >= Math.round(settings.getApiKeys().size() * 100 * USAGE_CAPACITY)) {
            long sleep = LocalDateTime.now().until(settings.getStart().plusMinutes(1), ChronoUnit.MILLIS);
            logger.info("Called api {} times, sleeping for {}", settings.getCount(), sleep);
            Thread.sleep(sleep);
        }
        return settings.getApiKey();
    }

    private Settings getSettings() {
        Optional<Settings> settings = settingsDao.findById(1L);
        return settings.orElseGet(() -> settingsDao.save(new Settings()));
    }
}
