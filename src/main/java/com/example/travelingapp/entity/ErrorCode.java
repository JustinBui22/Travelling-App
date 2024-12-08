package com.example.travelingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Error_Codes")
@Getter
@Setter
public class ErrorCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "errorId")
    private int errorId;

    @Column(name = "error_code", nullable = false, unique = true)
    private String errorCode;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "error_description")
    private String errorDescription;

    @Column(name = "createdD_date", unique = true)
    private LocalDate createdDate;

    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    @Column(name = "error_type")
    private String errorType;

    @Column(name = "http_code")
    private String httpCode;

    public ErrorCode() {

    }
}
