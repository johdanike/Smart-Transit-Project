package org.johdan.user.services.implementations;

import org.johdan.user.data.models.User;
import org.johdan.user.data.repositories.UserRepository;
import org.johdan.user.dtos.requests.RegisterUserRequest;
import org.johdan.user.dtos.responses.RegisterUserResponse;
import org.johdan.user.enums.Role;
import org.johdan.user.exceptions.UserExistsException;
import org.johdan.user.services.services.AuthenticationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RegisterUserResponse createNewUser(RegisterUserRequest registerUserRequest) {
        if (userRepository.findByEmail(registerUserRequest.getEmail()).isPresent()) throw new UserExistsException("User already exists!");
        User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setPassword(registerUserRequest.getPassword());
        user.setUserName(registerUserRequest.getUserName());
        user.setPhoneNumber(registerUserRequest.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole(Role.USER);
        user.setIsVerified(false);
        userRepository.save(user);

        RegisterUserResponse response = new RegisterUserResponse();
        response.setMessage("User registered successfully!");
        response.setUsername(user.getUsername());
        return response;
    }
}
