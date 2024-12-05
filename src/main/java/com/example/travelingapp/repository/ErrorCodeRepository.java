package com.example.travelingapp.repository;

import com.example.travelingapp.entity.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Long> {

    Optional<ErrorCode> findByErrorCode(String errorCode);
    Optional<ErrorCode> findByHttpCode(String httpStatus);
}
