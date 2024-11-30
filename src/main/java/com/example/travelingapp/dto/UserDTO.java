package com.example.travelingapp.dto;


import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;

    // Constructors
    public UserDTO() {}

    public UserDTO(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

}
