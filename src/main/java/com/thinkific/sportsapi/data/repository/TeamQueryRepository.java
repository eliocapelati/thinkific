package com.thinkific.sportsapi.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface TeamQueryRepository<T, ID> extends MongoRepository<T, ID> {
    Page<T> findAllByTeamId(String teamId, Pageable pageable);
    Optional<T> findByIdAndTeamId(ID id, String teamId);
}
