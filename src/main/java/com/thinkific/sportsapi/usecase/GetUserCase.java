package com.thinkific.sportsapi.usecase;


import com.thinkific.sportsapi.api.domain.UserResponse;
import com.thinkific.sportsapi.api.exception.UserNotFoundException;
import com.thinkific.sportsapi.data.domain.UsersEntity;
import com.thinkific.sportsapi.data.repository.UsersRepository;
import com.thinkific.sportsapi.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Lazy
public class GetUserCase {
    private final Logger log = LoggerFactory.getLogger(GetUserCase.class);
    private final UsersRepository usersRepository;
    private final UserMapper mapper;

    public GetUserCase(UsersRepository usersRepository, UserMapper mapper) {
        this.usersRepository = usersRepository;
        this.mapper = mapper;
    }

    public UserResponse handle(String email){

        final Page<UsersEntity> all = this.usersRepository.findAll(Pageable.unpaged());


        log.trace("Lookup user: {}", email);
        final UsersEntity entity = new UsersEntity();
        entity.setEmail(email);

        final Example<UsersEntity> example = Example.of(entity);

        return this.usersRepository
            .findOne(example)
            .map(mapper::from)
            .orElseThrow(UserNotFoundException::new);
    }
}
