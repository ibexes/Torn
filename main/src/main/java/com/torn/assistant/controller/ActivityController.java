package com.torn.assistant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.assistant.model.dto.UserActivityDTO;
import com.torn.assistant.service.FactionSyncService;
import com.torn.assistant.service.PlayerTrackerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivityController {
    private final PlayerTrackerService playerTrackerService;
    private final FactionSyncService factionSyncService;

    public ActivityController(PlayerTrackerService playerTrackerService, FactionSyncService factionSyncService) {
        this.playerTrackerService = playerTrackerService;
        this.factionSyncService = factionSyncService;
    }

    @GetMapping("/api/activity/user/{userId}")
    public List<UserActivityDTO> getActivity(@PathVariable(required = false) Long userId) {
        return playerTrackerService.getUserActivity(userId);
    }

    @GetMapping("/api/activity/faction/{factionId}")
    public List<Long> getMembers(@PathVariable Long factionId) {
        return playerTrackerService.getFactionMembers(factionId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/faction/sync/{factionId}")
    public void syncFaction(@PathVariable Long factionId) throws InterruptedException, TornApiAccessException, JsonProcessingException {
        factionSyncService.sync(factionId);
    }
}
