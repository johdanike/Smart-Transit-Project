package org.johdan.user.data.repositories;

import org.johdan.user.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phone);
    Optional<User> findByUsername(String userName);

    Optional<User> findByUserName(String userName);
}
