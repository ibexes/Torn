package com.torn.assistant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.assistant.model.dto.UserContributionDetailedDTO;
import com.torn.assistant.model.dto.UserContributionSummaryDTO;
import com.torn.assistant.service.FactionStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
public class FactionGymController {
    private static final Logger logger = LoggerFactory.getLogger(FactionGymController.class);
    private final FactionStatsService factionStatsService;

    public FactionGymController(FactionStatsService factionStatsService) {
        this.factionStatsService = factionStatsService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/faction/members/update")
    public ResponseEntity<String> updateMembers() throws JsonProcessingException, TornApiAccessException {
        factionStatsService.updateMembers();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/api/faction/contributions/intervals")
    public List<Date> getAvailableTimes(Principal principal) {
        return factionStatsService.getAvailableTimes(principal.getName());
    }

    @GetMapping("/api/faction/contributions/summary/{start}/{end}")
    public List<UserContributionSummaryDTO> getContributionSummary(Principal principal,
                                                                   @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date start,
                                                                   @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date end) {
        logger.info("Getting contribution summary between {} and {} for {}", start, end, principal.getName());
        return factionStatsService.getContributionSummary(principal.getName(), start, end);
    }

    @GetMapping("/api/faction/contributions/user/{userId}/{start}/{end}")
    public UserContributionDetailedDTO getContributionForUser(Principal principal, @PathVariable Long userId,
                                                              @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date start,
                                                              @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date end) {
        logger.info("Getting contribution data for user {} between {} and {}", userId, start, end);
        return factionStatsService.getUserContributionDetailedForUser(principal.getName(), userId, start, end);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/faction/contributions/poll")
    public void poll() {
        logger.info("Manual poll invoked");
        factionStatsService.run(false);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/faction/contributions/poll/force")
    public void forcePoll() {
        logger.info("Manual force poll invoked");
        factionStatsService.run(true);
    }
}
