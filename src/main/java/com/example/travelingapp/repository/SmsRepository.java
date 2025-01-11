package com.example.travelingapp.repository;

import com.example.travelingapp.entity.SmsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsRepository extends JpaRepository<SmsEntity, Long> {
    Optional<SmsEntity> findBySmsCodeAndSmsFlow (String smsCode, String smsFlow);
}
