package com.example.travelingapp.repository;

import com.example.travelingapp.entity.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Integer> {

    Optional<ErrorCode> findByErrorCodeAndFlow(String errorCode, String flow);
    Optional<ErrorCode> findByErrorCodeAndErrorEnumAndFlow(String errorCode, String errorEnum, String flow);
    Optional<ErrorCode> findByErrorEnum(String httpStatus);
}
