package com.thinkific.sportsapi.mapper;

import com.auth0.json.auth.TokenHolder;
import com.thinkific.sportsapi.api.domain.CreateUserRequest;
import com.thinkific.sportsapi.api.domain.LoginResponse;
import com.thinkific.sportsapi.api.domain.UserResponse;
import com.thinkific.sportsapi.data.domain.UsersEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UsersEntity from(CreateUserRequest request);

    LoginResponse from(TokenHolder holder);

    UserResponse from(UsersEntity entity);
}
