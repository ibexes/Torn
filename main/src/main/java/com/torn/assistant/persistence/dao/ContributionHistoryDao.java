package com.torn.assistant.persistence.dao;

import com.torn.assistant.persistence.entity.ContributionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ContributionHistoryDao extends JpaRepository<ContributionHistory, Long> {
    List<ContributionHistory> findByFetchedAtBetweenOrderByFetchedAtAsc(LocalDateTime start, LocalDateTime end);

    List<ContributionHistory> findAllByOrderByFetchedAtAsc();

    //TODO figure this out lol
//    @Query(value = "select h from contribution_history h join h.userActivities ua on ua.user.userId = :userId" +
//            " where h.fetchedAt >= :startDate and h.fetchedAt <= :endDate order by h.fetchedAt asc", nativeQuery = true)
//    List<ContributionHistory> findByUserAndFetchedAtBetweenOrderByFetchedAtAsc(@Param("userId") Long userId,
//                                                                               @Param("startDate") LocalDateTime startDate,
//                                                                               @Param("endDate") LocalDateTime endDate);
}
