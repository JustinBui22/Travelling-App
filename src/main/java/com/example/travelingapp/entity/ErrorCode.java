package com.example.travelingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Error_Codes")
@Getter
@Setter
public class ErrorCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ErrorId")
    private int errorId;

    @Column(name = "ErrorCode", nullable = false, unique = true)
    private String errorCode;

    @Column(name = "ErrorMessage", nullable = false)
    private String errorMessage;

    @Column(name = "CreatedDate", unique = true)
    private Date createdDate;

    @Column(name = "ModifiedDate")
    private Date modifiedDate;

    @Column(name = "ErrorType")
    private String errorType;

    @Column(name = "HttpCode")
    private String httpCode;

    public ErrorCode(String errorCode, String errorMessage, String httpCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpCode = httpCode;
    }

    public ErrorCode() {

    }
}
