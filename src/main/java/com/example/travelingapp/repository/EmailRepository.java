package com.example.travelingapp.repository;

import com.example.travelingapp.entity.EmailEntity;
import com.example.travelingapp.enums.EmailEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
    Optional<EmailEntity> findByEmailEnum(EmailEnum emailEnum);
}
