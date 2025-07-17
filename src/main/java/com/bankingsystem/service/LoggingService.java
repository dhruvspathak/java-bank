package com.bankingsystem.service;

import com.bankingsystem.model.Account;
import com.bankingsystem.util.EncryptionUtils;
import java.io.*;
import java.time.LocalDateTime;

public class LoggingService implements AutoCloseable {
    private final BufferedWriter bufferedWriter;
    private static final long MAX_LOG_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public LoggingService(String logPath) throws IOException {
        File file = new File(logPath).getAbsoluteFile();

        // Create file with secure permissions before using FileWriter
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                // Set restrictive directory permissions
                // NOTE: Java's setReadable/setWritable/setExecutable are best-effort and not secure on Windows.
                // For Unix systems, consider using java.nio.file.Files.setPosixFilePermissions for strict security.
                parentDir.setReadable(false, false);
                parentDir.setWritable(false, false);
                parentDir.setExecutable(false, false);
                parentDir.setReadable(true, true);   // Only owner can read
                parentDir.setWritable(true, true);   // Only owner can write
                parentDir.setExecutable(true, true); // Only owner can execute (for directory access)
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                // File is created with secure permissions
                // NOTE: On Windows, these are not strictly enforced. For Unix, use PosixFilePermission if possible.
                file.setReadable(false, false);
                file.setWritable(false, false);
                file.setExecutable(false, false);
                file.setReadable(true, true);   // Only owner can read
                file.setWritable(true, true);   // Only owner can write
            }
        }

        // Resource is properly managed by AutoCloseable implementation
        FileWriter fileWriter = new FileWriter(file, true);
        this.bufferedWriter = new BufferedWriter(fileWriter);
    }

    // Helper to mask UPI ID (show first 2 and last 2 chars, mask rest)
    private String maskUpiId(String upiId) {
        if (upiId == null || upiId.length() <= 4) return "[MASKED]";
        StringBuilder sb = new StringBuilder();
        sb.append(upiId.substring(0, 2));
        for (int i = 2; i < upiId.length() - 2; i++) sb.append('*');
        sb.append(upiId.substring(upiId.length() - 2));
        return sb.toString();
    }

    private boolean isLogFileSizeSafe(File file) {
        try {
            return file.length() < MAX_LOG_FILE_SIZE;
        } catch (SecurityException e) {
            System.err.println("Unable to check log file size due to security restrictions.");
            return false; // Treat as unsafe if we can't check
        }
    }

    public void logAccountCreation(Account account, String accountType, String upiId, int creditCard) {
        try {
            String logEntry;
            String maskedUpi = upiId != null ? maskUpiId(upiId) : "Not set";
            if (accountType.equals("Main")) {
                logEntry = String.format(
                        "[%s] %s Account created - ID: %s, Name: %s, Balance:  [1m [32m %d, UPI: %s, Card: %s\n",
                        LocalDateTime.now(),
                        accountType,
                        account.getAccountId(),
                        account.getName(),
                        account.getAmount(),
                        maskedUpi,
                        EncryptionUtils.encryptCreditCard(creditCard));
            } else {
                String parentAccNoForLog = account.getAccountId().substring(3);
                logEntry = String.format(
                        "[%s] %s Account created - Parent: MAIN%s, ID: %s, Name: %s, Balance:  [1m [32m %d, UPI: %s, Card: %s\n",
                        LocalDateTime.now(),
                        accountType,
                        parentAccNoForLog,
                        account.getAccountId(),
                        account.getName(),
                        account.getAmount(),
                        maskedUpi,
                        EncryptionUtils.encryptCreditCard(creditCard));
            }
            File logFile = new File(bufferedWriter.toString());
            if (isLogFileSizeSafe(logFile)) {
                bufferedWriter.write(logEntry);
                bufferedWriter.flush();
            } else {
                System.err.println("Log file size limit exceeded. Logging stopped for this session.");
            }
        } catch (IOException e) {
            System.err.println("Error logging account creation");
        }
    }

    public void logDeposit(Account account, int amount, int newBalance) {
        try {
            String logEntry = String.format(
                    "[%s] DEPOSIT - Account: %s, Amount:  [1m [32m %d, New Balance:  [1m [32m %d\n",
                    LocalDateTime.now(),
                    account.getAccountId(),
                    amount,
                    newBalance);
            File logFile = new File(bufferedWriter.toString());
            if (isLogFileSizeSafe(logFile)) {
                bufferedWriter.write(logEntry);
                bufferedWriter.flush();
            } else {
                System.err.println("Log file size limit exceeded. Logging stopped for this session.");
            }
        } catch (IOException e) {
            System.err.println("Error logging deposit");
        }
    }

    public void logWithdrawal(Account account, int amount, int newBalance, String method) {
        try {
            String logEntry = String.format(
                    "[%s] WITHDRAW (%s) - Account: %s, Amount:  [1m [32m %d, New Balance:  [1m [32m %d\n",
                    LocalDateTime.now(),
                    method,
                    account.getAccountId(),
                    amount,
                    newBalance);
            File logFile = new File(bufferedWriter.toString());
            if (isLogFileSizeSafe(logFile)) {
                bufferedWriter.write(logEntry);
                bufferedWriter.flush();
            } else {
                System.err.println("Log file size limit exceeded. Logging stopped for this session.");
            }
        } catch (IOException e) {
            System.err.println("Error logging withdrawal");
        }
    }

    public void logTaxPayment(Account account, double taxAmount) {
        try {
            String logEntry = String.format(
                    "[%s] TAX PAID - Account: %s, Amount:  [1m [32m %.2f\n",
                    LocalDateTime.now(),
                    account.getAccountId(),
                    taxAmount);
            File logFile = new File(bufferedWriter.toString());
            if (isLogFileSizeSafe(logFile)) {
                bufferedWriter.write(logEntry);
                bufferedWriter.flush();
            } else {
                System.err.println("Log file size limit exceeded. Logging stopped for this session.");
            }
        } catch (IOException e) {
            System.err.println("Error logging tax payment");
        }
    }

    public void logTransfer(Account fromAccount, Account toAccount, double amount) {
        try {
            String logEntry = String.format(
                    "[%s] TRANSFER - From: %s, To: %s, Amount:  [1m [32m %.2f\n",
                    LocalDateTime.now(),
                    fromAccount.getAccountId(),
                    toAccount.getAccountId(),
                    amount);
            File logFile = new File(bufferedWriter.toString());
            if (isLogFileSizeSafe(logFile)) {
                bufferedWriter.write(logEntry);
                bufferedWriter.flush();
            } else {
                System.err.println("Log file size limit exceeded. Logging stopped for this session.");
            }
        } catch (IOException e) {
            System.err.println("Error logging transfer");
        }
    }

    public void close() {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing log file");
        }
    }
}