package org.johdan.user.services.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.johdan.user.data.models.User;
import org.johdan.user.data.repositories.UserRepository;
import org.johdan.user.dtos.requests.LoginRequest;
import org.johdan.user.dtos.requests.RegisterUserRequest;
import org.johdan.user.dtos.responses.LoginResponse;
import org.johdan.user.dtos.responses.RegisterUserResponse;
import org.johdan.user.exceptions.UserDoesNotExistException;
import org.johdan.user.exceptions.WrongUserNameOrPasswordException;
import org.johdan.user.springSecurity.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    private RegisterUserRequest registerUserRequest;
    private LoginRequest loginRequest;


    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUserName("JD");
        registerUserRequest.setPassword("password");
        registerUserRequest.setEmail("jod@gmail.com");
        registerUserRequest.setPhoneNumber("80172028728");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("JD");
        loginRequest.setPassword("password");

    }


    @Test
    public void testThatUserCanSignUp(){
        RegisterUserResponse registerUserResponse = authenticationService.createNewUser(registerUserRequest);
        assertNotNull(registerUserResponse);
        User savedUser = userRepository.findByUserName("JD");
        assertNotNull(savedUser);
        assertEquals("jod@gmail.com", savedUser.getEmail());
        assertEquals("80172028728", savedUser.getPhoneNumber());
    }

    @Test
    public void testThatUserCannotRegisterTwice(){
        RegisterUserResponse registerUserResponse = authenticationService.createNewUser(registerUserRequest);
        assertNotNull(registerUserResponse);
        User savedUser = userRepository.findByUserName("JD");
        assertEquals("jod@gmail.com", savedUser.getEmail());

        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class, () -> authenticationService.createNewUser(registerUserRequest));
        assertEquals("User already exists!", exception.getMessage());
    }

    @Test
    public void testThatRegisteredUserCanLogin(){
        RegisterUserResponse response = authenticationService.createNewUser(registerUserRequest);
        assertNotNull(response);
        assertEquals("JD", response.getUsername());

        LoginResponse loginResponse = authenticationService.loginUser(loginRequest);
        assertEquals(loginResponse.getUserName(), "JD");
        assertEquals("Logged in successfully!", loginResponse.getMessage());
    }

    @Test
    public void testThatRegisteredUserCannotLoginWithWrongCredentials(){
        RegisterUserResponse registerUserResponse = authenticationService.createNewUser(registerUserRequest);
        assertNotNull(registerUserResponse);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("JD");
        loginRequest.setPassword("wrongPassword");

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authenticationService.loginUser(loginRequest));
        assertEquals("Bad credentials", exception.getMessage());

    }
}