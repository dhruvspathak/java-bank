package com.bankingsystem.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtils {
    // Use environment variable for encryption key, fallback to system property
    private static final String ENCRYPTION_KEY = getEncryptionKey();
    private static final String SALT = getSalt();

    private static String getEncryptionKey() {
        String key = System.getenv("BANK_ENCRYPTION_KEY");
        if (key == null || key.trim().isEmpty()) {
            key = System.getProperty("bank.encryption.key");
        }
        if (key == null || key.trim().isEmpty()) {
            throw new RuntimeException("Encryption key not set. Please set BANK_ENCRYPTION_KEY environment variable or bank.encryption.key system property.");
        }
        return key;
    }

    private static String getSalt() {
        String salt = System.getenv("BANK_ENCRYPTION_SALT");
        if (salt == null || salt.trim().isEmpty()) {
            salt = System.getProperty("bank.encryption.salt");
        }
        if (salt == null || salt.trim().isEmpty()) {
            throw new RuntimeException("Encryption salt not set. Please set BANK_ENCRYPTION_SALT environment variable or bank.encryption.salt system property.");
        }
        return salt;
    }

    public static String encryptCreditCard(int creditCardNumber) {
        if (creditCardNumber == -1) {
            return "Not set";
        }

        try {
            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    ENCRYPTION_KEY.toCharArray(),
                    SALT.getBytes(StandardCharsets.UTF_8),
                    65536,
                    256);
            javax.crypto.SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            SecureRandom random = new SecureRandom();
            byte[] nonce = new byte[12];
            random.nextBytes(nonce);
            javax.crypto.spec.GCMParameterSpec gcmSpec = new javax.crypto.spec.GCMParameterSpec(128, nonce);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            String cardNumberStr = String.valueOf(creditCardNumber);
            byte[] encryptedBytes = cipher.doFinal(cardNumberStr.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[nonce.length + encryptedBytes.length];
            System.arraycopy(nonce, 0, combined, 0, nonce.length);
            System.arraycopy(encryptedBytes, 0, combined, nonce.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);

        } catch (java.security.GeneralSecurityException e) {
            // Do not leak exception details or stack traces
            System.err.println("Encryption operation failed");
            return maskCreditCard(creditCardNumber);
        }
    }

    public static String maskCreditCard(int creditCardNumber) {
        if (creditCardNumber == -1) {
            return "Not set";
        }
        return "[MASKED]";
    }
}