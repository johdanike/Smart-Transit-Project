package org.johdan.user.services.services;

import org.johdan.user.data.models.User;
import org.johdan.user.dtos.requests.RegisterUserRequest;
import org.johdan.user.dtos.responses.RegisterUserResponse;

public interface AuthenticationService {

    RegisterUserResponse createNewUser(RegisterUserRequest registerUserRequest);
}
