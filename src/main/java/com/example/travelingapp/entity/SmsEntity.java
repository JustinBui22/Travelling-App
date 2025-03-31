package com.example.travelingapp.entity;

import com.example.travelingapp.enums.SmsEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Sms_Contents")
@Getter
@Setter
public class SmsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_id")
    private int smsId;

    @Column(name = "sms_code", nullable = false)
    private String smsCode;

    @Column(name = "sms_content", nullable = false)
    private String smsContent;

    @Column(name = "sms_flow", nullable = false)
    private String smsFlow;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "sms_enum", nullable = false)
    private SmsEnum smsEnum;
}

