package com.thinkific.sportsapi.usecase;


import com.thinkific.sportsapi.api.domain.users.UserResponse;
import com.thinkific.sportsapi.api.exception.NotFoundException;
import com.thinkific.sportsapi.data.domain.UsersEntity;
import com.thinkific.sportsapi.data.repository.UsersRepository;
import com.thinkific.sportsapi.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Lazy
public class GetUserCase {
    private static final String RESOURCE = "User";
    private final Logger log = LoggerFactory.getLogger(GetUserCase.class);
    private final UsersRepository usersRepository;
    private final UserMapper mapper;

    public GetUserCase(UsersRepository usersRepository, UserMapper mapper) {
        this.usersRepository = usersRepository;
        this.mapper = mapper;
    }

    public UserResponse handle(String email) {

        log.trace("Lookup user: {}", email);
        final UsersEntity entity = new UsersEntity(email);

        final Example<UsersEntity> example = Example.of(entity);

        final Optional<UsersEntity> optionalUser = this.usersRepository.findOne(example);

        log.trace("User was found {}", optionalUser.isPresent());

        return optionalUser
                .map(mapper::from)
                .orElseThrow(() -> new NotFoundException(RESOURCE));
    }
}
