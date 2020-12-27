package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.client.FactionApiClient;
import com.torn.api.model.faction.Contribution;
import com.torn.api.model.faction.Contributor;
import com.torn.api.model.faction.Member;
import com.torn.api.model.faction.Stat;
import com.torn.assistant.model.dto.ContributionHistoryDTO;
import com.torn.assistant.model.dto.StatDTO;
import com.torn.assistant.model.dto.UserContributionDTO;
import com.torn.assistant.model.dto.UserContributionSummaryDTO;
import com.torn.assistant.persistence.dao.ContributionHistoryDao;
import com.torn.assistant.persistence.dao.UserDao;
import com.torn.assistant.persistence.entity.ContributionHistory;
import com.torn.assistant.persistence.entity.User;
import com.torn.assistant.persistence.entity.UserContribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Collectors;

import static com.torn.api.client.FactionApiClient.getMembers;

@Service
public class FactionStatsService {
    public static final Logger logger = LoggerFactory.getLogger(FactionStatsService.class);
    private final ContributionHistoryDao contributionHistoryDao;
    private final UserDao userDao;
    private final String apiKey;

    public FactionStatsService(ContributionHistoryDao contributionHistoryDao, UserDao userDao, @Value("${API_KEY}") String apiKey) {
        this.contributionHistoryDao = contributionHistoryDao;
        this.userDao = userDao;
        this.apiKey = apiKey;
    }

    public List<Date> getAvailableTimes() {
        return contributionHistoryDao.findAll()
                .stream()
                .map(contributionHistory -> Date.from(contributionHistory.getFetchedAt().atZone(ZoneId.systemDefault()).toInstant()))
                .collect(Collectors.toList());
    }

    public List<UserContributionSummaryDTO> getContributionSummary(Date start, Date end) {
        List<ContributionHistory> fetchedContributions = contributionHistoryDao.findByFetchedAtBetweenOrderByFetchedAtAsc(LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()),
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

    private Long calculateDifference(Long end, Long start) {
        if (start == null) {
            if (end != null) {
                return end;
            }
        }
        if(end == null) {
            return 0L;
        }
        return end - start;
    }

    private Long sum(Long... stats) {
        long total = 0L;
        for (Long stat : stats) {
            if (stat != null) {
                total += stat;
            }
        }
        return total;
    }

    public List<ContributionHistoryDTO> getContributionHistory(Date start, Date end) {
        List<ContributionHistory> fetchedContributions = contributionHistoryDao.findByFetchedAtBetweenOrderByFetchedAtAsc(LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()));
        List<ContributionHistoryDTO> contributionHistoryDTOList = new ArrayList<>();

        for (ContributionHistory contributionHistory : fetchedContributions) {
            ContributionHistoryDTO contributionHistoryDTO = new ContributionHistoryDTO(Date.from(contributionHistory.getFetchedAt().atZone(ZoneId.systemDefault()).toInstant()));
            List<UserContributionDTO> userContributionDTOList = new ArrayList<>();

            for (UserContribution userContribution : contributionHistory.getUserActivities()) {
                UserContributionDTO userContributionDTO = new UserContributionDTO(userContribution.getUser().getUserId(),
                        userContribution.getUser().getName());

                userContributionDTO.setGymDefence(userContribution.getGymDefence());
                userContributionDTO.setGymDexterity(userContribution.getGymDexterity());
                userContributionDTO.setGymSpeed(userContribution.getGymSpeed());
                userContributionDTO.setGymStrength(userContribution.getGymStrength());
                userContributionDTOList.add(userContributionDTO);
            }
            contributionHistoryDTO.setUserContributions(userContributionDTOList);
            contributionHistoryDTOList.add(contributionHistoryDTO);
        }
        return contributionHistoryDTOList;
    }

    public void updateMembers() throws JsonProcessingException {
        List<Member> members = getMembers(apiKey);
        for (Member member : members) {
            User user = this.userDao.findByUserId(member.getUserId()).orElse(new User());
            if (user.getName() == null || user.getUserId() == null) {
                user.setName(member.getName());
                user.setUserId(member.getUserId());
                userDao.save(user);
            }
        }
    }

    @Scheduled(cron = "${STATS_CRON:0 0 */1 * * ?}")
    @Transactional
    public void run() throws JsonProcessingException {
        logger.info("I am running");

        ContributionHistory contributionHistory = new ContributionHistory();
        LocalDateTime fetchedAt = LocalDateTime.now().withSecond(0).withNano(0);

        Map<Long, UserContribution> userContributionMap = new HashMap<>();

        Contribution speedContribution = FactionApiClient.getContribution(apiKey, Stat.SPEED);
        Contribution dexterityContribution = FactionApiClient.getContribution(apiKey, Stat.DEXTERITY);
        Contribution strengthContribution = FactionApiClient.getContribution(apiKey, Stat.STRENGTH);
        Contribution defenceContribution = FactionApiClient.getContribution(apiKey, Stat.DEFENCE);

        speedContribution.getContributors().forEach(
                contributor -> {
                    UserContribution userContribution = getUserContribution(userContributionMap, contributor);
                    userContribution.setGymSpeed(contributor.getContribution());
                }
        );

        dexterityContribution.getContributors().forEach(
                contributor -> {
                    UserContribution userContribution = getUserContribution(userContributionMap, contributor);
                    userContribution.setGymDexterity(contributor.getContribution());
                }
        );

        strengthContribution.getContributors().forEach(
                contributor -> {
                    UserContribution userContribution = getUserContribution(userContributionMap, contributor);
                    userContribution.setGymStrength(contributor.getContribution());
                }
        );

        defenceContribution.getContributors().forEach(
                contributor -> {
                    UserContribution userContribution = getUserContribution(userContributionMap, contributor);
                    userContribution.setGymDefence(contributor.getContribution());
                }
        );

        contributionHistory.setUserActivities(new ArrayList<>(userContributionMap.values()));
        contributionHistory.setFetchedAt(fetchedAt);

        contributionHistoryDao.save(contributionHistory);
    }

    private UserContribution getUserContribution(Map<Long, UserContribution> userContributionMap, Contributor contributor) {
        Long userId = contributor.getMember().getUserId();
        UserContribution userContribution = userContributionMap.getOrDefault(userId,
                new UserContribution(userDao.findByUserId(userId).orElse(new User(userId, contributor.getMember().getName()))));

        userContribution.setLastAction(contributor.getMember().getLastAction());

        if (!userContributionMap.containsKey(userId)) {
            userContributionMap.put(userId, userContribution);
        }
        return userContribution;
    }
}
