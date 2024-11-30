package com.example.travelingapp.repository;

import com.example.travelingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists by their username.
     *
     * @param username the username to check for
     * @return true if a user exists with the given username, otherwise false
     */
    boolean existsByUsername(String username);
}
