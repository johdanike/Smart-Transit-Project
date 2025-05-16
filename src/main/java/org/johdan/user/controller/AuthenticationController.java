package org.johdan.user.controller;

import org.johdan.user.data.repositories.UserRepository;
import org.johdan.user.dtos.requests.AuthenticationRequest;
import org.johdan.user.dtos.responses.AuthenticationResponse;
import org.johdan.user.enums.Role;
import org.johdan.user.springSecurity.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtUtil jwtUtil,
                                    PasswordEncoder passwordEncoder,
                                    UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            Role role = user.getRole();

            String token = jwtUtil.generateToken(user.getEmail(), role);

            return ResponseEntity.ok(new AuthenticationResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }


}
