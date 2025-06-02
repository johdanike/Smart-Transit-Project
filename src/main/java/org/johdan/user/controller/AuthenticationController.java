package org.johdan.user.controller;

import org.johdan.user.data.models.User;
import org.johdan.user.data.repositories.UserRepository;
import org.johdan.user.dtos.responses.AuthenticationResponse;
import org.johdan.user.enums.Role;
import org.johdan.user.services.implementations.OtpService;
import org.johdan.user.springSecurity.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OtpService otpService;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtUtil jwtUtil,
                                    PasswordEncoder passwordEncoder,
                                    UserRepository userRepository, OtpService otpService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

        this.otpService = otpService;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> req) {
        String code = otpService.generateOtp(req.get("phoneNumber"));
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> req) {
        String phone = req.get("phoneNumber");
        String code = req.get("otpCode");

        if (otpService.verifyOtp(phone, code)) {
            User user = userRepository.findByPhoneNumber(phone)
                    .orElseGet(() -> userRepository.save(new User()));

            String jwt = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
            return ResponseEntity.ok(Map.of("token", jwt));
        }

        return ResponseEntity.status(401).body("Invalid OTP");
    }



}
