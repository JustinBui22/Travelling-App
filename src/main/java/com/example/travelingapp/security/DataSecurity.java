package com.example.travelingapp.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSecurity {
    public String password;

    public enum encryptionAlgorithm {

    }

    public DataSecurity(String password, String encryptionAlgorithm) {
        this.password = password;
    }

    public DataSecurity() {
    }

    public String encryptData(String password) {
        String encryptedPassword = password;
        return encryptedPassword;
    }

    public String decryptData(String encryptedPassword) {
        String decryptedPassword = encryptedPassword;
        return decryptedPassword;
    }
}
