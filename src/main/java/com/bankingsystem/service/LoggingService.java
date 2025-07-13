package com.bankingsystem.service;

import com.bankingsystem.model.Account;
import com.bankingsystem.util.EncryptionUtils;
import java.io.*;
import java.time.LocalDateTime;

public class LoggingService implements AutoCloseable {
    private final BufferedWriter bufferedWriter;

    public LoggingService(String logPath) throws IOException {
        File file = new File(logPath).getAbsoluteFile();

        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                parentDir.setReadable(false, false);
                parentDir.setWritable(false, false);
                parentDir.setExecutable(false, false);
                parentDir.setReadable(true, true);
                parentDir.setWritable(true, true);
                parentDir.setExecutable(true, true);
            }

            file.createNewFile();
            file.setReadable(false, false);
            file.setWritable(false, false);
            file.setExecutable(false, false);
            file.setReadable(true, true);
            file.setWritable(true, true);
        }

        // Resource is properly managed by AutoCloseable implementation
        FileWriter fileWriter = new FileWriter(file, true);
        this.bufferedWriter = new BufferedWriter(fileWriter);
    }

    public void logAccountCreation(Account account, String accountType, String upiId, int creditCard) {
        try {
            String logEntry;
            if (accountType.equals("Main")) {
                logEntry = String.format(
                        "[%s] %s Account created - ID: %s, Name: %s, Balance: ₹%d, UPI: %s, Card: %s\n",
                        LocalDateTime.now(),
                        accountType,
                        account.getAccountId(),
                        account.getName(),
                        account.getAmount(),
                        upiId != null ? upiId : "Not set",
                        EncryptionUtils.encryptCreditCard(creditCard));
            } else {
                String parentAccNoForLog = account.getAccountId().substring(3);
                logEntry = String.format(
                        "[%s] %s Account created - Parent: MAIN%s, ID: %s, Name: %s, Balance: ₹%d, UPI: %s, Card: %s\n",
                        LocalDateTime.now(),
                        accountType,
                        parentAccNoForLog,
                        account.getAccountId(),
                        account.getName(),
                        account.getAmount(),
                        upiId != null ? upiId : "Not set",
                        EncryptionUtils.encryptCreditCard(creditCard));
            }
            bufferedWriter.write(logEntry);
            bufferedWriter.flush();
        } catch (IOException e) {
            System.err.println("Error logging account creation: " + e.getMessage());
        }
    }

    public void logDeposit(Account account, int amount, int newBalance) {
        try {
            String logEntry = String.format(
                    "[%s] DEPOSIT - Account: %s, Amount: ₹%d, New Balance: ₹%d\n",
                    LocalDateTime.now(),
                    account.getAccountId(),
                    amount,
                    newBalance);
            bufferedWriter.write(logEntry);
            bufferedWriter.flush();
        } catch (IOException e) {
            System.err.println("Error logging deposit: " + e.getMessage());
        }
    }

    public void logWithdrawal(Account account, int amount, int newBalance, String method) {
        try {
            String logEntry = String.format(
                    "[%s] WITHDRAW (%s) - Account: %s, Amount: ₹%d, New Balance: ₹%d\n",
                    LocalDateTime.now(),
                    method,
                    account.getAccountId(),
                    amount,
                    newBalance);
            bufferedWriter.write(logEntry);
            bufferedWriter.flush();
        } catch (IOException e) {
            System.err.println("Error logging withdrawal: " + e.getMessage());
        }
    }

    public void logTaxPayment(Account account, double taxAmount) {
        try {
            String logEntry = String.format(
                    "[%s] TAX PAID - Account: %s, Amount: ₹%.2f\n",
                    LocalDateTime.now(),
                    account.getAccountId(),
                    taxAmount);
            bufferedWriter.write(logEntry);
            bufferedWriter.flush();
        } catch (IOException e) {
            System.err.println("Error logging tax payment: " + e.getMessage());
        }
    }

    public void logTransfer(Account fromAccount, Account toAccount, double amount) {
        try {
            String logEntry = String.format(
                    "[%s] TRANSFER - From: %s, To: %s, Amount: ₹%.2f\n",
                    LocalDateTime.now(),
                    fromAccount.getAccountId(),
                    toAccount.getAccountId(),
                    amount);
            bufferedWriter.write(logEntry);
            bufferedWriter.flush();
        } catch (IOException e) {
            System.err.println("Error logging transfer: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing log file: " + e.getMessage());
        }
    }
}