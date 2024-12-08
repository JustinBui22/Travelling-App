package com.example.travelingapp.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataAesAlgorithm {
    public String password;

    public enum encryptionAlgorithm {

    }

    public DataAesAlgorithm(String password, String encryptionAlgorithm) {
        this.password = password;
    }

    public DataAesAlgorithm() {
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
