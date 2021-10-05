package com.thinkific.sportsapi.data.repository;

import com.thinkific.sportsapi.data.domain.PlayersEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@Lazy
public interface PlayersRepository extends MongoRepository<PlayersEntity, String> {
    Page<PlayersEntity> findAllByTeamId(String teamId, Pageable pageable);
    Optional<PlayersEntity> findByIdAndTeamId(String id, String teamId);
}
