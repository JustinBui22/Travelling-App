package com.example.travelingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_check")
@Getter
@Setter
public class OtpCheckEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 12L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "phone_num")
    private String phoneNumber;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "newest_otp")
    private String newestOtp;

    @Column(name = "is_block", nullable = false)
    private boolean isBlock;

    @Column(name = "otp_expiration_time")
    private LocalDateTime otpExpirationTime;

    @Column(name = "otp_restricted_time")
    private LocalDateTime otpRestrictedTime;

    public OtpCheckEntity() {
    }

    public OtpCheckEntity(String username, String email, LocalDate createdDate, String phoneNumber, int retryCount, String newestOtp, boolean isBlock) {
        this.username = username;
        this.email = email;
        this.createdDate = createdDate;
        this.phoneNumber = phoneNumber;
        this.retryCount = retryCount;
        this.newestOtp = newestOtp;
        this.isBlock = isBlock;
    }
}

