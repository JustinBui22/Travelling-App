package com.example.travelingapp.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataEncrypt {
    public String password;

    public enum encryptionAlgorithm {

    }

    public DataEncrypt(String password, String encryptionAlgorithm) {
        this.password = password;
    }

    public DataEncrypt() {
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
