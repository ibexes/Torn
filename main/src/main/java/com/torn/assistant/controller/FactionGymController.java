package com.torn.assistant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.assistant.model.dto.ContributionHistoryDTO;
import com.torn.assistant.model.dto.UserContributionSummaryDTO;
import com.torn.assistant.service.FactionStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class FactionGymController {
    private static final Logger logger = LoggerFactory.getLogger(FactionGymController.class);
    private final FactionStatsService factionStatsService;

    public FactionGymController(FactionStatsService factionStatsService) {
        this.factionStatsService = factionStatsService;
    }

    @GetMapping("/api/faction/members/update")
    public void updateMembers() throws JsonProcessingException {
        factionStatsService.updateMembers();
    }

    @GetMapping("/api/faction/contributions/intervals")
    public List<Date> getAvailableTimes() {
        return factionStatsService.getAvailableTimes();
    }

    @GetMapping("/api/faction/contributions/summary/{start}/{end}")
    public List<UserContributionSummaryDTO> getContributionSummary(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date start,
                                                                   @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date end) {
        logger.info("Getting contribution summary between {} and {}", start, end);
        return factionStatsService.getContributionSummary(start, end);
    }

    @GetMapping("/api/faction/contributions/data/{start}/{end}")
    public List<ContributionHistoryDTO> getContributionData(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date start,
                                                            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date end) {
        logger.info("Getting contribution data between {} and {}", start, end);
        return factionStatsService.getContributionHistory(start, end);
    }

    @GetMapping("/api/faction/contributions/poll")
    public void poll() throws JsonProcessingException {
        logger.info("Manual poll invoked");
        factionStatsService.run();
    }
}
