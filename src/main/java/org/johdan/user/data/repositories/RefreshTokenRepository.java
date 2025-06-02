package org.johdan.user.data.repositories;

import org.johdan.user.data.models.RefreshToken;
import org.johdan.user.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
