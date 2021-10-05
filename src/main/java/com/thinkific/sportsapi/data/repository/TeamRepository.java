package com.thinkific.sportsapi.data.repository;

import com.thinkific.sportsapi.data.domain.TeamEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@Lazy
public interface TeamRepository extends MongoRepository<TeamEntity, String> {
    Optional<TeamEntity> findByIdAndUserId(String id, String userId);
}
