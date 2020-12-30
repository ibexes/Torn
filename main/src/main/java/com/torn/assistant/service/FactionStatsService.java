package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.client.FactionApiClient;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Contribution;
import com.torn.api.model.faction.Contributor;
import com.torn.api.model.faction.Member;
import com.torn.api.model.faction.Stat;
import com.torn.assistant.model.dto.DataPointDTO;
import com.torn.assistant.model.dto.StatDTO;
import com.torn.assistant.model.dto.UserContributionDetailedDTO;
import com.torn.assistant.model.dto.UserContributionSummaryDTO;
import com.torn.assistant.persistence.dao.ContributionHistoryDao;
import com.torn.assistant.persistence.dao.FactionDao;
import com.torn.assistant.persistence.dao.UserDao;
import com.torn.assistant.persistence.entity.ContributionHistory;
import com.torn.assistant.persistence.entity.Faction;
import com.torn.assistant.persistence.entity.User;
import com.torn.assistant.persistence.entity.UserContribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.torn.api.client.FactionApiClient.getMembers;
import static com.torn.assistant.utils.CollectionUtils.getRandomElement;
import static com.torn.assistant.utils.DateUtils.toDate;
import static com.torn.assistant.utils.MathUtils.calculateDifference;
import static com.torn.assistant.utils.MathUtils.sum;

@Service
public class FactionStatsService {
    public static final Logger logger = LoggerFactory.getLogger(FactionStatsService.class);
    private final ContributionHistoryDao contributionHistoryDao;
    private final FactionService factionService;
    private final FactionDao factionDao;
    private final UserDao userDao;

    public FactionStatsService(ContributionHistoryDao contributionHistoryDao, FactionService factionService,
                               FactionDao factionDao, UserDao userDao) {
        this.contributionHistoryDao = contributionHistoryDao;
        this.factionService = factionService;
        this.factionDao = factionDao;
        this.userDao = userDao;
    }

    public List<Date> getAvailableTimes(String username) {
        return contributionHistoryDao.findByFactionEqualsOrderByFetchedAtAsc(factionService.getFaction(username))
                .stream()
                .map(contributionHistory -> Date.from(contributionHistory.getFetchedAt().atZone(ZoneId.systemDefault()).toInstant()))
                .collect(Collectors.toList());
    }

    public UserContributionDetailedDTO getUserContributionDetailedForUser(String username, Long userId, Date start, Date end) {
        List<ContributionHistory> fetchedContributions = contributionHistoryDao
                .findByFactionEqualsAndFetchedAtBetweenOrderByFetchedAtAsc(factionService.getFaction(username),
                        LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()));

        UserContributionDetailedDTO contributionDetailedDTO = new UserContributionDetailedDTO();
        if (!fetchedContributions.isEmpty()) {
            Optional<UserContribution> first = getContributionForUser(fetchedContributions.get(0).getUserActivities(), userId);
            if (!first.isPresent()) {
                return contributionDetailedDTO;
            }
            contributionDetailedDTO.setUserId(first.get().getUser().getUserId());
            contributionDetailedDTO.setName(first.get().getUser().getName());
            Long startStrength = first.get().getGymStrength();
            Long startGymDefence = first.get().getGymDefence();
            Long startDexterity = first.get().getGymDexterity();
            Long startGymSpeed = first.get().getGymSpeed();
            Long startGymTotal = sum(startDexterity, startGymDefence, startGymSpeed, startStrength);

            for (ContributionHistory history : fetchedContributions) {
                if (!history.getUserActivities().isEmpty()) {
                    Optional<UserContribution> optionalUserContribution = getContributionForUser(history.getUserActivities(), userId);
                    if (!optionalUserContribution.isPresent()) {
                        continue;
                    }
                    UserContribution userContribution = optionalUserContribution.get();
                    Long currentGymTotal = sum(userContribution.getGymDefence(), userContribution.getGymDexterity(), userContribution.getGymSpeed(), userContribution.getGymStrength());
                    DataPointDTO strength = new DataPointDTO(toDate(history.getFetchedAt()), calculateDifference(userContribution.getGymStrength(), startStrength));
                    DataPointDTO speed = new DataPointDTO(toDate(history.getFetchedAt()), calculateDifference(userContribution.getGymSpeed(), startGymSpeed));
                    DataPointDTO defence = new DataPointDTO(toDate(history.getFetchedAt()), calculateDifference(userContribution.getGymDefence(), startGymDefence));
                    DataPointDTO dexterity = new DataPointDTO(toDate(history.getFetchedAt()), calculateDifference(userContribution.getGymDexterity(), startDexterity));
                    DataPointDTO total = new DataPointDTO(toDate(history.getFetchedAt()), calculateDifference(currentGymTotal, startGymTotal));

                    contributionDetailedDTO.getGymStrength().add(strength);
                    contributionDetailedDTO.getGymDexterity().add(dexterity);
                    contributionDetailedDTO.getGymDefence().add(defence);
                    contributionDetailedDTO.getGymSpeed().add(speed);
                    contributionDetailedDTO.getGymTotal().add(total);
                }
            }
        }

        return contributionDetailedDTO;
    }

    public Optional<UserContribution> getContributionForUser(List<UserContribution> userContributions, Long userId) {
        return userContributions.parallelStream().filter(userContribution -> userContribution.getUser().getUserId().equals(userId))
                .findFirst();
    }

    public List<UserContributionSummaryDTO> getContributionSummary(String username, Date start, Date end) {
        List<ContributionHistory> fetchedContributions = contributionHistoryDao
                .findByFactionEqualsAndFetchedAtBetweenOrderByFetchedAtAsc(factionService.getFaction(username),
                        LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()));
        Map<Long, UserContributionSummaryDTO> userContributionSummaryDTOMap = new HashMap<>();

        if (!fetchedContributions.isEmpty()) {
            ContributionHistory earliest = fetchedContributions.get(0);
            ContributionHistory latest = fetchedContributions.get(fetchedContributions.size() - 1);

            for (UserContribution userContribution : earliest.getUserActivities()) {
                Long userId = userContribution.getUser().getUserId();
                UserContributionSummaryDTO userContributionSummaryDTO = userContributionSummaryDTOMap
                        .getOrDefault(userId, new UserContributionSummaryDTO(userId, userContribution.getUser().getName()));

                userContributionSummaryDTO.getGymDefence().setStart(userContribution.getGymDefence());
                userContributionSummaryDTO.getGymDexterity().setStart(userContribution.getGymDexterity());
                userContributionSummaryDTO.getGymSpeed().setStart(userContribution.getGymSpeed());
                userContributionSummaryDTO.getGymStrength().setStart(userContribution.getGymStrength());
                userContributionSummaryDTO.getGymTotal().setStart(sum(userContribution.getGymStrength(),
                        userContribution.getGymDefence(), userContribution.getGymDexterity(), userContribution.getGymSpeed()));

                if (!userContributionSummaryDTOMap.containsKey(userId)) {
                    userContributionSummaryDTOMap.put(userId, userContributionSummaryDTO);
                }
            }

            for (UserContribution userContribution : latest.getUserActivities()) {
                Long userId = userContribution.getUser().getUserId();
                UserContributionSummaryDTO userContributionSummaryDTO = userContributionSummaryDTOMap
                        .getOrDefault(userId, new UserContributionSummaryDTO(userId, userContribution.getUser().getName()));

                StatDTO defence = userContributionSummaryDTO.getGymDefence();
                StatDTO strength = userContributionSummaryDTO.getGymStrength();
                StatDTO speed = userContributionSummaryDTO.getGymSpeed();
                StatDTO dexterity = userContributionSummaryDTO.getGymDexterity();

                defence.setEnd(userContribution.getGymDefence());
                dexterity.setEnd(userContribution.getGymDexterity());
                speed.setEnd(userContribution.getGymSpeed());
                strength.setEnd(userContribution.getGymStrength());
                userContributionSummaryDTO.getGymTotal().setEnd(sum(userContribution.getGymStrength(),
                        userContribution.getGymDefence(), userContribution.getGymDexterity(), userContribution.getGymSpeed()));
                userContributionSummaryDTO.setLastAction(userContribution.getLastAction());

                if (!userContributionSummaryDTOMap.containsKey(userId)) {
                    userContributionSummaryDTOMap.put(userId, userContributionSummaryDTO);
                } else {
                    defence.setDifference(calculateDifference(defence.getEnd(), defence.getStart()));
                    strength.setDifference(calculateDifference(strength.getEnd(), strength.getStart()));
                    speed.setDifference(calculateDifference(speed.getEnd(), speed.getStart()));
                    dexterity.setDifference(calculateDifference(dexterity.getEnd(), dexterity.getStart()));
                    userContributionSummaryDTO.getGymTotal().setDifference(sum(defence.getDifference(), strength.getDifference(), speed.getDifference(), dexterity.getDifference()));
                }
            }
        }

        return new ArrayList<>(userContributionSummaryDTOMap.values());
    }

    public void updateMembers() throws JsonProcessingException, TornApiAccessException {
        for (Faction faction : factionDao.findByTrackContributionsIsTrue()) {
            List<Member> members = getMembers(getRandomElement(faction.getApiKey()));

            for (Member member : members) {
                User user = this.userDao.findByUserId(member.getUserId()).orElse(new User());
                if (user.getName() == null || user.getUserId() == null || !faction.equals(user.getFaction())) {
                    user.setName(member.getName());
                    user.setUserId(member.getUserId());
                    user.setFaction(faction);
                    userDao.save(user);
                }
            }
        }
    }

    @Scheduled(cron = "${STATS_CRON:0 0 */1 * * ?}")
    @Transactional
    public void run() throws JsonProcessingException, TornApiAccessException {
        for (Faction faction : factionDao.findByTrackContributionsIsTrue()) {
            logger.info("Updating contribution stats for {}", faction.getName());
            Set<String> apiKeys = faction.getApiKey();

            if (apiKeys.isEmpty()) {
                logger.warn("There are no api keys for {}", faction.getName());
                continue;
            }

            ContributionHistory contributionHistory = new ContributionHistory(faction);
            LocalDateTime fetchedAt = LocalDateTime.now().withSecond(0).withNano(0);

            Map<Long, UserContribution> userContributionMap = new HashMap<>();

            Contribution speedContribution = FactionApiClient.getContribution(getRandomElement(apiKeys), faction.getId(), Stat.SPEED);
            Contribution dexterityContribution = FactionApiClient.getContribution(getRandomElement(apiKeys), faction.getId(), Stat.DEXTERITY);
            Contribution strengthContribution = FactionApiClient.getContribution(getRandomElement(apiKeys), faction.getId(), Stat.STRENGTH);
            Contribution defenceContribution = FactionApiClient.getContribution(getRandomElement(apiKeys), faction.getId(), Stat.DEFENCE);

            speedContribution.getContributors().forEach(
                    contributor -> {
                        UserContribution userContribution = getUserContribution(faction, userContributionMap, contributor);
                        userContribution.setGymSpeed(contributor.getContribution());
                    }
            );

            dexterityContribution.getContributors().forEach(
                    contributor -> {
                        UserContribution userContribution = getUserContribution(faction, userContributionMap, contributor);
                        userContribution.setGymDexterity(contributor.getContribution());
                    }
            );

            strengthContribution.getContributors().forEach(
                    contributor -> {
                        UserContribution userContribution = getUserContribution(faction, userContributionMap, contributor);
                        userContribution.setGymStrength(contributor.getContribution());
                    }
            );

            defenceContribution.getContributors().forEach(
                    contributor -> {
                        UserContribution userContribution = getUserContribution(faction, userContributionMap, contributor);
                        userContribution.setGymDefence(contributor.getContribution());
                    }
            );

            contributionHistory.setUserActivities(new ArrayList<>(userContributionMap.values()));
            contributionHistory.setFetchedAt(fetchedAt);

            contributionHistoryDao.save(contributionHistory);
        }
    }

    private UserContribution getUserContribution(Faction faction, Map<Long, UserContribution> userContributionMap, Contributor contributor) {
        Long userId = contributor.getMember().getUserId();
        User user = userDao.findByUserId(userId).orElse(new User(userId, contributor.getMember().getName()));
        if (!faction.equals(user.getFaction())) {
            user.setFaction(faction);
        }

        UserContribution userContribution = userContributionMap.getOrDefault(userId,
                new UserContribution(user));

        userContribution.setLastAction(contributor.getMember().getLastAction());

        if (!userContributionMap.containsKey(userId)) {
            userContributionMap.put(userId, userContribution);
        }
        return userContribution;
    }
}
