package com.example.travelingapp.repository;

import com.example.travelingapp.entity.ErrorCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCodeEntity, Integer> {

    Optional<ErrorCodeEntity> findByErrorEnumAndFlow(String errorEnum, String flow);
    Optional<ErrorCodeEntity> findFirstByErrorEnum(String errorEnum);
}
