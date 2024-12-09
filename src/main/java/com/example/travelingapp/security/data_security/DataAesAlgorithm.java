package com.example.travelingapp.security.data_security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import static com.example.travelingapp.enums.CommonEnum.AES;

@Getter
@Setter
@Log4j2
public class DataAesAlgorithm {
    private SecretKey generateKey(int n) {
        try {
            log.info("Start generating random key using {} algorithm", AES.name());
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES.name());
            keyGenerator.init(n);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            log.error("There is an error generating random key!", e);
        }
        log.info("Key generated unsuccessful!");
        return null;
    }

    private static SecretKey generateKeyFromInput(String input) {
        try {
            log.info("Start generating key from input using {} algorithm", AES.name());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(input.toCharArray(), Objects.requireNonNull(Base64.getDecoder().decode(generateSaltFromInput(input))), 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec)
                    .getEncoded(), AES.name());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("There is an error generating key from input!", e);
        }
        log.info("Key generated from input unsuccessful!");
        return null;
    }

    private IvParameterSpec generateIV() {
        // Add randomness to the encryption process.
        // Ensures that encrypting the same plaintext with the same key results in different ciphertexts.
        // Must be unique for each encryption operation
        // AES is a block cipher that operates on 128-bit (16-byte) blocks
        // The IV must always be 16 bytes (128 bits), because AES processes data in 128-bit blocks
        try {
            log.info("Start generating a random pseudo-random value IV using {} algorithm", AES.name());
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            return new IvParameterSpec(iv);
        } catch (Exception e) {
            log.error("There is an error generating IV!", e);
        }
        log.info("IV random generated unsuccessful!");
        return null;
    }

    private static IvParameterSpec generateIVFromInput(String input) {
        // Add randomness to the encryption process.
        // Ensures that encrypting the same plaintext with the same key results in different ciphertexts.
        // Must be unique for each encryption operation
        // AES is a block cipher that operates on 128-bit (16-byte) blocks
        // The IV must always be 16 bytes (128 bits), because AES processes data in 128-bit blocks
        try {
            log.info("Start generating a pseudo-random value IV from an input using {} algorithm", AES.name());
            // Create SHA-256 hash of the input
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            // Truncate the hash to 16 bytes (128 bits) for the IV
            byte[] iv = Arrays.copyOf(hash, 16);

            // Create an IvParameterSpec with the generated IV
            return new IvParameterSpec(iv);
        } catch (Exception e) {
            log.error("There is an error generating IV from input!", e);
        }
        log.info("IV generated from input unsuccessful!");
        return null;
    }

    private String generateSalt() {
        // Optimal Length: 16 to 32 bytes
        // Adds randomness to the key derivation process
        // Even if two users have the same password, the derived keys will differ due to unique salts.
        // Salt is critical to prevent precomputed attacks like rainbow tables.
        try {
            log.info("Start generating a random secure length-byte salt for {} algorithm", AES.name());
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (Exception e) {
            log.error("There is an error generating salt!", e);
        }
        log.info("Salt generated unsuccessful!");
        return null;
    }

    private static String generateSaltFromInput(String input) {
        // Optimal Length: 16 to 32 bytes
        // Adds randomness to the key derivation process
        // Even if two users have the same password, the derived keys will differ due to unique salts.
        // Salt is critical to prevent precomputed attacks like rainbow tables.
        try {
            log.info("Start generating a secure length-byte salt from input for {} algorithm", AES.name());
            // Create MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform hashing
            byte[] hash = digest.digest(input.getBytes());
            // Truncate to 16 bytes (128 bits) for AES salt
            byte[] salt = Arrays.copyOf(hash, 16);
            return Base64.getEncoder().encodeToString(salt);
        } catch (Exception e) {
            log.error("There is an error generating salt from input!", e);
        }
        log.info("Salt generated from input unsuccessful!");
        return null;
    }

    public static String encryptData(String input) {
        try {
            log.info("Start encrypting data using {} algorithm", AES.name());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, generateKeyFromInput(input), generateIVFromInput(input
            ));
            byte[] encryptedText = cipher.doFinal(input.getBytes());
            return Base64.getEncoder()
                    .encodeToString(encryptedText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            log.error("There is an error encrypting data!", e);
        }
        log.info("Data encryption unsuccessful!");
        return null;
    }

    private String decryptData(String input, SecretKey key,
                              IvParameterSpec iv) {

        try {
            log.info("Start decrypting data using {} algorithm", AES.name());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedText = cipher.doFinal(Base64.getDecoder()
                    .decode(input));
            return new String(decryptedText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            log.error("There is an error decrypting data!", e);
        }
        log.info("Data decryption unsuccessful!");
        return null;
    }
}
