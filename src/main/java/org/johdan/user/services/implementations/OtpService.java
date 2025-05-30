package org.johdan.user.services.implementations;

import org.johdan.user.data.models.OtpToken;
import org.johdan.user.data.repositories.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private OtpTokenRepository otpTokenRepository;

    public String generateOtp(String phoneNumber) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        OtpToken otp = new OtpToken();
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(code);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setIsUsed(false);

        otpTokenRepository.save(otp);

        return code;
    }

    private void sendOtpEmail(String to, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your smartTransit OTP Code");
        message.setText("Your OTP code is: " + otpCode + "\n\nIt expires in 5 minutes.");

        mailSender.send(message);
    }

    public boolean verifyOtp(String phoneNumber, String otpCode) {
        OtpToken token = otpTokenRepository.findOtpByOtpCodeAndUserId(phoneNumber, otpCode);
        if (token == null) return false;
        boolean isValid = token.getOtpCode().equals(otpCode) &&
                token.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5));
        if (isValid) {
            token.setIsUsed(true);
            otpTokenRepository.save(token);
        }
        return isValid;
    }
}

