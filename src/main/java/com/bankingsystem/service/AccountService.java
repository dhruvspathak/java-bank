package com.bankingsystem.service;

import com.bankingsystem.model.*;
import com.bankingsystem.util.ValidationUtils;
import com.bankingsystem.util.EncryptionUtils;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AccountService {
    private final LoggingService loggingService;

    public AccountService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public Account createMainAccount(String accNo, String name, int amount, String upiId, int creditCard) {
        Account account = MainAccount.createAccount(accNo, name, amount, upiId, creditCard);
        loggingService.logAccountCreation(account, "Main", upiId, creditCard);
        return account;
    }

    public Account createSavingsAccount(String parentAccNo, String name, int amount, String upiId, int creditCard) {
        Account account = SavingsAccount.createAccount(parentAccNo, name, amount, upiId, creditCard);
        loggingService.logAccountCreation(account, "Savings", upiId, creditCard);
        return account;
    }

    public Account createCurrentAccount(String parentAccNo, String name, int amount, String upiId, int creditCard) {
        Account account = CurrentAccount.createAccount(parentAccNo, name, amount, upiId, creditCard);
        loggingService.logAccountCreation(account, "Current", upiId, creditCard);
        return account;
    }

    public void displaySecureAccountInfo(Account account) {
        System.out.println("Account Details:");
        System.out.println("  Account Number: " + account.getAccountId());
        System.out.println("  Name: " + account.getName());
        System.out.println("  Balance: " + account.getAmount());
        // Note: UPI ID and Credit Card are not accessible from outside the package
        // This is intentional for security - sensitive data should not be exposed
        System.out.println("  UPI ID: [SECURED]");
        System.out.println("  Credit Card: [SECURED]");
    }
}