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
        Account account;
        
        if (upiId != null && creditCard != -1) {
            account = new MainAccount(accNo, name, amount, upiId, creditCard);
        } else if (upiId != null) {
            account = new MainAccount(accNo, name, amount, upiId);
        } else if (creditCard != -1) {
            account = new MainAccount(accNo, name, amount, creditCard);
        } else {
            account = new MainAccount(accNo, name, amount);
        }
        
        loggingService.logAccountCreation(account, "Main", upiId, creditCard);
        return account;
    }
    
    public Account createSavingsAccount(String parentAccNo, String name, int amount, String upiId, int creditCard) {
        Account account;
        
        if (upiId != null && creditCard != -1) {
            account = new SavingsAccount(parentAccNo, name, amount, upiId, creditCard);
        } else if (upiId != null) {
            account = new SavingsAccount(parentAccNo, name, amount, upiId);
        } else if (creditCard != -1) {
            account = new SavingsAccount(parentAccNo, name, amount, creditCard);
        } else {
            account = new SavingsAccount(parentAccNo, name, amount);
        }
        
        loggingService.logAccountCreation(account, "Savings", upiId, creditCard);
        return account;
    }
    
    public Account createCurrentAccount(String parentAccNo, String name, int amount, String upiId, int creditCard) {
        Account account;
        
        if (upiId != null && creditCard != -1) {
            account = new CurrentAccount(parentAccNo, name, amount, upiId, creditCard);
        } else if (upiId != null) {
            account = new CurrentAccount(parentAccNo, name, amount, upiId);
        } else if (creditCard != -1) {
            account = new CurrentAccount(parentAccNo, name, amount, creditCard);
        } else {
            account = new CurrentAccount(parentAccNo, name, amount, creditCard);
        }
        
        loggingService.logAccountCreation(account, "Current", upiId, creditCard);
        return account;
    }
    
    public void displaySecureAccountInfo(Account account) {
        System.out.println("Account Details:");
        System.out.println("  Account Number: " + account.acc_no);
        System.out.println("  Name: " + account.getName());
        System.out.println("  Balance: " + account.getAmount());
        if (account.upi_id != null) {
            System.out.println("  UPI ID: " + account.upi_id);
        }
        if (account.credit_card_no != -1) {
            System.out.println("  Credit Card: [SECURED]");
        }
    }
} 