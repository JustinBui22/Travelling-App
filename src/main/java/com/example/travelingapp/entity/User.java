package com.example.travelingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User implements Serializable, UserDetails {
    @Serial
    private static final long serialVersionUID = 12L;

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

    @Column(name = "dob", nullable = false)

    private LocalDate dob;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "referred_code")
    private String referredCode;

    @Column(name = "phone_num")
    private String phoneNumber;

    @Column(name = "status", nullable = false)
    private boolean status;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String password, String phoneNumber, LocalDate dob, LocalDate createdDate, String email, boolean status) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.createdDate = createdDate;
        this.email = email;
        this.status = status;
    }

    public User() {

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
