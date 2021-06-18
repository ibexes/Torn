package com.torn.assistant.service;

import com.torn.assistant.persistence.dao.SettingsDao;
import com.torn.assistant.persistence.entity.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Wrapper client to keep retrying calls if they fail
 */
@Service
public class TornApiService {
    private static final Logger logger = LoggerFactory.getLogger(TornApiService.class);
    private static final Double USAGE_CAPACITY = 0.6; // use 60%

    private final SettingsDao settingsDao;

    public TornApiService(SettingsDao settingsDao) {
        this.settingsDao = settingsDao;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized String getApiKey(Settings settings) throws InterruptedException {
        settings.getStart();
        if (settings.getCount() >= Math.round(settings.getApiKeys().size() * 100 * USAGE_CAPACITY)) {
            long sleep = LocalDateTime.now().until(settings.getStart().plusMinutes(1), ChronoUnit.MILLIS);
            logger.info("Called api {} times, sleeping for {}", settings.getCount(), sleep);
            Thread.sleep(sleep);
        }
        String apiKey = settings.getApiKey();
        settingsDao.save(settings);
        return apiKey;
    }

    public Settings getSettings() {
        Optional<Settings> settings = settingsDao.findById(1L);
        return settings.orElseGet(() -> settingsDao.save(new Settings()));
    }
}
