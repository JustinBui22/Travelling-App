package com.example.travelingapp.entity;

import com.example.travelingapp.enums.EmailEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "email_contents")
@Getter
@Setter
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id")
    private int emailId;

    @Column(name = "email_code", nullable = false)
    private String emailCode;

    @Column(name = "email_content", columnDefinition = "TEXT", nullable = false)
    private String emailContent;

    @Column(name = "email_flow", nullable = false)
    private String emailFlow;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_enum", nullable = false)
    private EmailEnum emailEnum;

    @Column(name = "email_subject", nullable = false)
    private String emailSubject;

    public EmailEntity(String emailCode, String emailContent, String emailFlow) {
        this.emailCode = emailCode;
        this.emailContent = emailContent;
        this.emailFlow = emailFlow;
    }

    public EmailEntity() {
    }
}

