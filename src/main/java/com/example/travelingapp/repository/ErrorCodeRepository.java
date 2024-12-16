package com.example.travelingapp.repository;

import com.example.travelingapp.entity.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Integer> {

    Optional<ErrorCode> findByErrorEnumAndFlow(String errorEnum, String flow);
}
