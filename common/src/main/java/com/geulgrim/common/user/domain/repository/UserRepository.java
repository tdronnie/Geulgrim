package com.geulgrim.common.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.geulgrim.common.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(Long userId);

    boolean existsByEmail(String email);

    boolean existsByUserId(Long userId);
}
