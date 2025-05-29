package org.johdan.user.services.services;

import jakarta.transaction.Transactional;
import org.johdan.user.data.models.User;
import org.johdan.user.data.repositories.UserRepository;
import org.johdan.user.dtos.requests.RegisterUserRequest;
import org.johdan.user.dtos.responses.RegisterUserResponse;
import org.johdan.user.springSecurity.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

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


    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUserName("JD");
        registerUserRequest.setPassword("password");
        registerUserRequest.setEmail("jod@gmail.com");
        registerUserRequest.setPhoneNumber("80172028728");


    }


    @Test
    public void test_thatUserCanSignUp(){
        RegisterUserResponse registerUserResponse = authenticationService.createNewUser(registerUserRequest);
        assertNotNull(registerUserResponse);
        User savedUser = userRepository.findByUserName("JD").orElse(null);
        assertNotNull(savedUser);
        assertEquals("jod@gmail.com", savedUser.getEmail());
        assertEquals("80172028728", savedUser.getPhoneNumber());
        assertTrue(passwordEncoder.matches("password", savedUser.getPassword()));
    }
}