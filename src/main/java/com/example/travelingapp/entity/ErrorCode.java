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

    @Column(name = "error_code", nullable = false)
    private String errorCode;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "error_description")
    private String errorDescription;

    @Column(name = "createdD_date")
    private LocalDate createdDate;

    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    @Column(name = "error_type")
    private String errorType;

    @Column(name = "error_enum", nullable = false)
    private String errorEnum;

    @Column(name = "http_code", nullable = false)
    private int httpCode;

    @Column(name = "flow")
    private String flow;

    public ErrorCode() {

    }
}
