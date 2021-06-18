package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Member;
import com.torn.assistant.persistence.dao.UserDao;
import com.torn.assistant.persistence.entity.Settings;
import com.torn.assistant.persistence.entity.User;
import com.torn.assistant.persistence.service.FactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.torn.api.client.FactionApiClient.getMembers;

@Service
public class FactionSyncService {
    private static final Logger logger = LoggerFactory.getLogger(FactionSyncService.class);
    private final FactionService factionService;
    private final TornApiService tornApiService;
    private final UserDao userDao;

    public FactionSyncService(FactionService factionService, TornApiService tornApiService, UserDao userDao) {
        this.factionService = factionService;
        this.tornApiService = tornApiService;
        this.userDao = userDao;
    }

    @Transactional
    public void sync(Long factionId) throws InterruptedException, TornApiAccessException, JsonProcessingException {
        int count = 0;
        List<User> users = this.factionService.getMembers(factionId);
        Settings settings = tornApiService.getSettings();

        List<Member> memberList = getMembers(tornApiService.getApiKey(settings), factionId);
        List<Long> memberIds = memberList.stream().map(Member::getUserId).collect(Collectors.toList());
        for (User user : users) {
            if (!memberIds.contains(user.getUserId())) {
                user.setFaction(null);
                userDao.save(user);
                count ++;
            }
        }

        logger.info("Removed {} from {}", count, factionId);
    }

}
