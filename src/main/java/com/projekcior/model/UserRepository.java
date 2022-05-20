package com.projekcior.model;

import com.projekcior.AuthUserHelper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    default Optional<User> findAuthenticatedUser() {
        var username = AuthUserHelper.getAuthenticatedUsername();
        return findByUsername(username);
    }
}
