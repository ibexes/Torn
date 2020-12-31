package com.torn.assistant.model.dto;

import com.torn.api.model.faction.OrganisedCrimeType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class OrganisedCrimeSummaryDTO implements Serializable {
    Map<OrganisedCrimeType, OrganisedCrimeStatDTO> stats;

    public OrganisedCrimeSummaryDTO() {
        stats = new EnumMap<>(OrganisedCrimeType.class);
        Arrays.stream(OrganisedCrimeType.values())
                .forEach(organisedCrimeType -> stats.put(organisedCrimeType, new OrganisedCrimeStatDTO(organisedCrimeType.getName())));
    }

    public Map<OrganisedCrimeType, OrganisedCrimeStatDTO> getStats() {
        return stats;
    }

    public void setStats(Map<OrganisedCrimeType, OrganisedCrimeStatDTO> stats) {
        this.stats = stats;
    }
}
