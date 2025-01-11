package com.example.travelingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "session_token_store")
@Getter
@Setter
public class SessionTokenStoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    public SessionTokenStoreEntity(String userName, String token, String sessionId, LocalDate createdDate) {
        this.userName = userName;
        this.token = token;
        this.sessionId = sessionId;
        this.createdDate = createdDate;
    }

    public SessionTokenStoreEntity() {

    }
}
