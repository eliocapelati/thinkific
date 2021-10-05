package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.users.CreateUserRequest;
import com.thinkific.sportsapi.api.exception.AlreadyExistsException;
import com.thinkific.sportsapi.data.domain.UsersEntity;
import com.thinkific.sportsapi.data.repository.UsersRepository;
import com.thinkific.sportsapi.mapper.UserMapper;
import com.thinkific.sportsapi.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;


@Service
@Lazy
public class SignupUserCase {

    private final Logger log = LoggerFactory.getLogger(LoginCase.class);
    private final UsersRepository usersRepository;
    private final UserMapper mapper;
    private final AuthService authService;

    public SignupUserCase(final UsersRepository usersRepository,
                          final UserMapper mapper,
                          final AuthService authService) {
        this.usersRepository = usersRepository;
        this.mapper = mapper;
        this.authService = authService;
    }

    public void handle(CreateUserRequest request) {
        checkUserExists(request.getEmail());

        final UsersEntity user = mapper.from(request);

        authService.createUser(request);
        usersRepository.save(user);

        log.info("Successful saved user {}", user.getEmail());
    }

    private void checkUserExists(String email) {
        final UsersEntity usersEntity = new UsersEntity();
        usersEntity.setEmail(email);
        Example<UsersEntity> example = Example.of(usersEntity);
        if (usersRepository.exists(example)) {
            log.debug("User with email {} already exists", email);
            throw new AlreadyExistsException();
        }
    }

}
