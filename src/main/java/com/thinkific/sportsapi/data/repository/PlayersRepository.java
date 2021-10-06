package com.thinkific.sportsapi.data.repository;

import com.thinkific.sportsapi.data.domain.PlayersEntity;
import org.springframework.context.annotation.Lazy;

import java.util.List;


@Lazy
public interface PlayersRepository extends TeamQueryRepository<PlayersEntity, String> {

    List<PlayersEntity> findAllByIdInAndTeamId(Iterable<String> id, String teamId);

}
