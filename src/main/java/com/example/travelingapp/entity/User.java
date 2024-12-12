package com.example.travelingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "dob")

    private LocalDate dob;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "referred_code")
    private String referredCode;

    @Column(name = "phone_num")
    private String phoneNumber;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String password, String phoneNumber, LocalDate dob, LocalDate createdDate, String email) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.createdDate = createdDate;
        this.email = email;
    }

    public User() {

    }
}
