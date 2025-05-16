package org.johdan.user.data.repositories;

import org.johdan.user.data.models.EveryUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<EveryUser, Long> {
    Optional<EveryUser> findByEmail(String email);
}
