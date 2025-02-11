package com.example.travelingapp.repository;

import com.example.travelingapp.entity.SessionTokenStoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionTokenRepository extends JpaRepository<SessionTokenStoreEntity, Long> {
    Optional<SessionTokenStoreEntity> findByUserNameAndSessionToken(String username, String sessionToken);

    List<SessionTokenStoreEntity> findAllByUserName(String username);

}
