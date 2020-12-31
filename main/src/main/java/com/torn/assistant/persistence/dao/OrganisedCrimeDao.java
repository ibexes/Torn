package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.OrganisedCrime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisedCrimeDao extends JpaRepository<OrganisedCrime, Long> {
}
