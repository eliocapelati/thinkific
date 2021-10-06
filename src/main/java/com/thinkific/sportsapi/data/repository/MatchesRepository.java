package com.thinkific.sportsapi.data.repository;

import com.thinkific.sportsapi.data.domain.MatchesEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


@Lazy
public interface MatchesRepository extends TeamQueryRepository<MatchesEntity, String> {
    Page<MatchesEntity> findAllByTeamIdAndDateAfter(String teamId, Pageable pageable, LocalDateTime date);
    Page<MatchesEntity> findAllByTeamIdAndDateBefore(String teamId, Pageable pageable, LocalDateTime date);
}
