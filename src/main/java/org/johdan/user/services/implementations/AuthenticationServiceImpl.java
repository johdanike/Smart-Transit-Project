package org.johdan.user.services.implementations;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.johdan.user.data.models.RefreshToken;
import org.johdan.user.data.models.User;
import org.johdan.user.data.repositories.RefreshTokenRepository;
import org.johdan.user.data.repositories.UserRepository;
import org.johdan.user.dtos.requests.LoginRequest;
import org.johdan.user.dtos.requests.RegisterUserRequest;
import org.johdan.user.dtos.responses.LoginResponse;
import org.johdan.user.dtos.responses.RegisterUserResponse;
import org.johdan.user.enums.Role;
import org.johdan.user.exceptions.UserDoesNotExistException;
import org.johdan.user.exceptions.WrongUserNameOrPasswordException;
import org.johdan.user.services.services.AuthenticationService;
import org.johdan.user.springSecurity.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.hibernate.query.sqm.tree.SqmNode.log;
//
//@Service
//public class AuthenticationServiceImpl implements AuthenticationService {
//
//    private final UserRepository userRepository;
//
//    public AuthenticationServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public RegisterUserResponse createNewUser(RegisterUserRequest registerUserRequest) {
//        if (userRepository.findByEmail(registerUserRequest.getEmail()).isPresent()) throw new UserDoesNotExistException("User already exists!");
//        User user = new User();
//        user.setEmail(registerUserRequest.getEmail());
//        user.setPassword(registerUserRequest.getPassword());
//        user.setUserName(registerUserRequest.getUserName());
//        user.setPhoneNumber(registerUserRequest.getPhoneNumber());
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//        user.setRole(Role.USER);
//        user.setIsVerified(false);
//        user.setIsLoggedIn(false);
//        userRepository.save(user);
//
//        RegisterUserResponse response = new RegisterUserResponse();
//        response.setMessage("User registered successfully!");
//        response.setUsername(user.getUsername());
//        return response;
//    }
//
//    @Override
//    public LoginResponse loginUser(LoginRequest loginRequest) {
//        checkLoginDetails(loginRequest);
//        User user = userRepository.findByUserName(loginRequest.getUsername());
//        if (user == null) throw new UserDoesNotExistException("User not found!");
//        if (!user.getPassword().equals(loginRequest.getPassword())) throw new WrongUserNameOrPasswordException("Wrong username or password!");
//        user.setIsLoggedIn(true);
//
//        LoginResponse response = new LoginResponse();
//        response.setMessage("Logged in successfully!");
//        response.setUserName(user.getUsername());
//        response.setIsLoggedIn(user.getIsLoggedIn());
//
//
//        return response;
//    }

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository           userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            JwtUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository        = userRepository;
        this.passwordEncoder       = passwordEncoder;
        this.authManager           = authManager;
        this.jwtUtil               = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RegisterUserResponse createNewUser(RegisterUserRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new UserDoesNotExistException("User already exists!");
        String hashed = passwordEncoder.encode(req.getPassword());

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(hashed);
        user.setUserName(req.getUserName());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setRole(Role.USER);
        user.setIsVerified(false);
        user.setIsLoggedIn(false);
        userRepository.save(user);

        RegisterUserResponse registerUserResponse = new RegisterUserResponse();
        registerUserResponse.setMessage("User registered successfully!");
        registerUserResponse.setUsername(user.getUsername());
        return registerUserResponse;
    }

    @Override
    public LoginResponse loginUser(LoginRequest loginReq) {
        checkLoginDetails(loginReq);
        withWhiteSpace(loginReq.getUsername());
        withWhiteSpace(loginReq.getPassword());

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReq.getUsername(),
                        loginReq.getPassword()
                )
        );

        String username = auth.getName();
//        String username = loginReq.getUsername();
        log.info("Found user = " +username);
        User user = userRepository.findByUserName(username);
        log.info("Username of user " + user.getUsername());

        String accessToken  = jwtUtil.generateAccessToken(username, user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(username);

        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshTokenExpiryMs()));
        refreshTokenRepository.save(token);

        user.setIsLoggedIn(true);
        userRepository.save(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserName(user.getUsername());
        loginResponse.setMessage("Logged in successfully!");
        loginResponse.setAccessToken(accessToken);
//        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setIsLoggedIn(true);
        return loginResponse;
    }

    private void checkLoginDetails(LoginRequest req) {
        if (req.getUsername() == null || req.getUsername().trim().isEmpty() ||
                req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Username or password cannot be empty");
        }
    }


    private static boolean withWhiteSpace(String username) {
        Pattern pattern = Pattern.compile("(.*?)\\s(.*?)");
        Matcher matcher = pattern.matcher(username);
        return matcher.find();
    }

}
