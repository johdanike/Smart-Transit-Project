package org.johdan.user.data.repositories;

import org.johdan.user.data.models.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    OtpToken findOtpByOtpCodeAndUserId(String otpCode, String userId);
}
