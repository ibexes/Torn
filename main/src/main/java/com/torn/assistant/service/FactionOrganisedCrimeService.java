package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.assistant.model.dto.OrganisedCrimeStatDTO;
import com.torn.assistant.model.dto.OrganisedCrimeSummaryDTO;
import com.torn.assistant.persistence.dao.FactionDao;
import com.torn.assistant.persistence.dao.OrganisedCrimeDao;
import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.OrganisedCrime;
import com.torn.assistant.persistence.entity.User;
import com.torn.assistant.persistence.service.FactionService;
import com.torn.assistant.persistence.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.torn.api.client.FactionApiClient.getOrganisedCrimes;
import static com.torn.assistant.utils.CollectionUtils.getRandomElement;

@Service
public class FactionOrganisedCrimeService {
    private static final Logger logger = LoggerFactory.getLogger(FactionOrganisedCrimeService.class);
    private final OrganisedCrimeDao organisedCrimeDao;
    private final FactionDao factionDao;
    private final UserService userService;
    private final FactionService factionService;

    public FactionOrganisedCrimeService(OrganisedCrimeDao organisedCrimeDao, FactionDao factionDao, UserService userService, FactionService factionService) {
        this.organisedCrimeDao = organisedCrimeDao;
        this.factionDao = factionDao;
        this.userService = userService;
        this.factionService = factionService;
    }

    public Date getStartTrackingDate(String username) {
        Optional<OrganisedCrime> crimeOptional = organisedCrimeDao
                .findTopByFactionEqualsOrderByPlannedAtAsc(factionService.getFaction(username));
        if (!crimeOptional.isPresent()) {
            return new Date();
        } else {
            return crimeOptional.get().getPlannedAt();
        }
    }

    public OrganisedCrimeSummaryDTO getCrimesSummary(String username, Date start, Date end) {
        Faction faction = factionService.getFaction(username);
        List<OrganisedCrime> organisedCrimes = organisedCrimeDao.findByFactionEqualsAndPlannedAtBetween(faction, start, end);

        OrganisedCrimeSummaryDTO summaryDTO = new OrganisedCrimeSummaryDTO();
        organisedCrimes.forEach(organisedCrime -> {
            OrganisedCrimeStatDTO statDTO = summaryDTO.getStats().get(organisedCrime.getCrimeType());
            statDTO.setAttempts(statDTO.getAttempts() + 1);
            if (Boolean.TRUE.equals(organisedCrime.getSuccess())) {
                statDTO.setSuccesses(statDTO.getSuccesses() + 1);
            }
        });
        return summaryDTO;
    }

    @Scheduled(cron = "${ORGANISED_CRIME_CRON:0 30 * * * ?}")
    @Transactional
    public void run() throws JsonProcessingException, TornApiAccessException {
        for (Faction faction : factionDao.findByTrackOrganisedCrimesIsTrue()) {
            logger.info("Updating OCs for {}", faction.getName());
            if (faction.getApiKey().isEmpty()) {
                logger.warn("There are no api keys for {}", faction.getName());
                continue;
            }
            List<com.torn.api.model.faction.OrganisedCrime> organisedCrimeList = getOrganisedCrimes(getRandomElement(faction.getApiKey()));
            for (com.torn.api.model.faction.OrganisedCrime organisedCrime : organisedCrimeList) {
                OrganisedCrime organisedCrimeEntity = new OrganisedCrime();
                organisedCrimeEntity.setId(organisedCrime.getId());
                organisedCrimeEntity.setCrimeType(organisedCrime.getCrimeType());
                organisedCrimeEntity.setInitiated(organisedCrime.getInitiated());
                if (organisedCrime.getInitiated()) {
                    organisedCrimeEntity.setInitiatedAt(organisedCrime.getInitiatedAt());
                    organisedCrimeEntity.setInitiatedBy(organisedCrime.getInitiatedBy());
                    organisedCrimeEntity.setSuccess(organisedCrime.getSuccess());
                    organisedCrimeEntity.setRespectGained(organisedCrime.getRespectGained());
                    organisedCrimeEntity.setMoneyGained(organisedCrime.getMoneyGained());
                }
                organisedCrimeEntity.setPlannedAt(organisedCrime.getPlannedAt());
                organisedCrimeEntity.setPlannedBy(organisedCrime.getPlannedBy());
                organisedCrimeEntity.setReadyAt(organisedCrime.getReadyAt());
                organisedCrimeEntity.setFaction(faction);

                Set<User> participants = new HashSet<>();
                for (Long userId : organisedCrime.getParticipants()) {
                    User user = userService.findByUserId(userId).orElse(new User(userId, "unknown user"));
                    participants.add(user);
                }

                organisedCrimeEntity.setParticipants(participants);
                organisedCrimeDao.save(organisedCrimeEntity);
            }
        }
    }
}
