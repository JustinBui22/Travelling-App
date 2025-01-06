package com.example.travelingapp.repository;

import com.example.travelingapp.entity.SessionTokenStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionTokenRepository extends JpaRepository<SessionTokenStore, Long> {
    Optional<SessionTokenStore> findByUserNameOrderByCreatedDate(String username);

    List<SessionTokenStore> findAllByUserName(String username);

}
