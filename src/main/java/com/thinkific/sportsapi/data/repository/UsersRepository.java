package com.thinkific.sportsapi.data.repository;

import com.thinkific.sportsapi.data.domain.UsersEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;

@Lazy
public interface UsersRepository extends MongoRepository<UsersEntity, String> {
}
