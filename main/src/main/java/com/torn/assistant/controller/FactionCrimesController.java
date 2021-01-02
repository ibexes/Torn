package com.torn.assistant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.assistant.model.dto.OrganisedCrimeSummaryDTO;
import com.torn.assistant.model.dto.UserDTO;
import com.torn.assistant.service.FactionOrganisedCrimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.Set;

@RestController
public class FactionCrimesController {
    private static final Logger logger = LoggerFactory.getLogger(FactionCrimesController.class);
    private final FactionOrganisedCrimeService factionOrganisedCrimeService;

    public FactionCrimesController(FactionOrganisedCrimeService factionOrganisedCrimeService) {
        this.factionOrganisedCrimeService = factionOrganisedCrimeService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/faction/ocs/poll")
    public void poll() throws JsonProcessingException, TornApiAccessException {
        logger.info("Manual OC poll invoked");
        factionOrganisedCrimeService.run();
    }

    @GetMapping("/api/faction/ocs/startDate")
    public Date getEarliestDate(Principal principal) {
        return factionOrganisedCrimeService.getStartTrackingDate(principal.getName());
    }

    @GetMapping("/api/faction/ocs/members")
    public Set<UserDTO> getParticipantUsers(Principal principal) {
        return factionOrganisedCrimeService.getParticipantUsers(principal.getName());
    }

    @GetMapping("/api/faction/ocs/summary/{userId}/{start}/{end}")
    public OrganisedCrimeSummaryDTO getContributionSummary(Principal principal, @PathVariable Long userId,
                                                           @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date start,
                                                           @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date end) {
        logger.info("Getting OC summary between {} and {} for {}", start, end, principal.getName());
        return factionOrganisedCrimeService.getCrimesSummary(principal.getName(), userId, start, end);
    }

    @GetMapping("/api/faction/ocs/summary/{start}/{end}")
    public OrganisedCrimeSummaryDTO getContributionSummary(Principal principal,
                                                                 @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date start,
                                                                 @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date end) {
        logger.info("Getting OC summary between {} and {} for {}", start, end, principal.getName());
        return factionOrganisedCrimeService.getCrimesSummary(principal.getName(), start, end);
    }
}
