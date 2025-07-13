package com.bankingsystem.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtils {
    private static final String ENCRYPTION_KEY = "BankSystemSecretKey2024!";

    public static String encryptCreditCard(int creditCardNumber) {
        if (creditCardNumber == -1) {
            return "Not set";
        }

        try {
            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    ENCRYPTION_KEY.toCharArray(),
                    "BankSystemSalt2024".getBytes(StandardCharsets.UTF_8),
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

        } catch (Exception e) {
            System.err.println("Encryption failed: " + e.getMessage());
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