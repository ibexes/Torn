package com.torn.assistant.model.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ContributionHistoryDTO implements Serializable {
    private Date fetchedAt;
    private List<UserContributionDTO> userContributions;

    public ContributionHistoryDTO(Date fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public Date getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Date fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public List<UserContributionDTO> getUserContributions() {
        return userContributions;
    }

    public void setUserContributions(List<UserContributionDTO> userContributionDTOList) {
        this.userContributions = userContributionDTOList;
    }
}
