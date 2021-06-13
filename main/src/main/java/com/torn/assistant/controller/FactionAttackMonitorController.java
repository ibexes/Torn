package com.torn.assistant.controller;

import com.torn.assistant.service.FactionAttackMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class FactionAttackMonitorController {
    private static final Logger logger = LoggerFactory.getLogger(FactionAttackMonitorController.class);
    private final FactionAttackMonitorService factionAttackMonitorService;


    public FactionAttackMonitorController(FactionAttackMonitorService factionAttackMonitorService) {
        this.factionAttackMonitorService = factionAttackMonitorService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/faction/attacks/{start}/{end}")
    public void manualUpdate(Principal principal, @PathVariable String start, @PathVariable String end) {
        logger.info("Manually updating attacks {} and {} for {}", start, end, principal.getName());
        factionAttackMonitorService.fetchUpdatesBetween(start, end);
    }
}
