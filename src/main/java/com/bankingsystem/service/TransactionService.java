package com.bankingsystem.service;

import com.bankingsystem.model.*;
import com.bankingsystem.util.ValidationUtils;
import java.util.Scanner;

public class TransactionService {
    private final LoggingService loggingService;

    public TransactionService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public int processDeposit(Account account, Scanner scanner) {
        int depositAmount;
        while (true) {
            System.out.print("Enter deposit amount: ");
            String input = scanner.nextLine();
            depositAmount = ValidationUtils.validateNumericInput(input, 1, Integer.MAX_VALUE);
            if (depositAmount != -1) break;
            System.out.println("Invalid deposit amount. Please enter a positive number.");
        }
        int newBalance = account.deposit(depositAmount);
        loggingService.logDeposit(account, depositAmount, newBalance);
        return newBalance;
    }

    public int processWithdrawal(Account account, Scanner scanner) {
        System.out.println("Withdrawal Options:");
        System.out.println("1. Simple Withdraw");
        System.out.println("2. Withdraw with UPI");
        System.out.println("3. Withdraw with Credit Card");
        int withdrawChoice = -1;
        while (true) {
            System.out.print("Enter choice (1-3): ");
            String input = scanner.nextLine();
            try {
                withdrawChoice = Integer.parseInt(input.trim());
                if (withdrawChoice >= 1 && withdrawChoice <= 3) break;
            } catch (NumberFormatException e) {}
            System.out.println("Invalid choice! Please enter 1-3.");
        }
        int withdrawAmount;
        while (true) {
            System.out.print("Enter withdrawal amount: ");
            String input = scanner.nextLine();
            withdrawAmount = ValidationUtils.validateNumericInput(input, 1, Integer.MAX_VALUE);
            if (withdrawAmount != -1) break;
            System.out.println("Invalid withdrawal amount. Please enter a positive number.");
        }
        int finalBalance = 0;
        String withdrawMethod = "";
        switch (withdrawChoice) {
            case 1:
                finalBalance = account.withdraw(withdrawAmount);
                withdrawMethod = "Simple";
                break;
            case 2:
                String withdrawUpi;
                while (true) {
                    System.out.print("Enter UPI ID: ");
                    withdrawUpi = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getUpiPattern());
                    if (withdrawUpi != null) break;
                    System.out.println("Invalid UPI ID format. Please use format: username@bank");
                }
                finalBalance = account.withdraw(withdrawUpi, withdrawAmount);
                withdrawMethod = "UPI";
                break;
            case 3:
                int withdrawCard;
                while (true) {
                    System.out.print("Enter credit card number: ");
                    withdrawCard = ValidationUtils.readSecureCreditCard(scanner);
                    if (withdrawCard != -1) break;
                    System.out.println("Invalid credit card number format.");
                }
                finalBalance = account.withdraw(withdrawCard, withdrawAmount);
                withdrawMethod = "Credit Card";
                break;
            default:
                System.out.println("Invalid choice!");
                return -1;
        }
        loggingService.logWithdrawal(account, withdrawAmount, finalBalance, withdrawMethod);
        return finalBalance;
    }

    public boolean processTaxPayment(Account account, Scanner scanner) {
        if (account instanceof Taxable) {
            Taxable taxableAccount = (Taxable) account;
            double taxAmount = taxableAccount.calculateTax();
            System.out.println("Tax amount: Rs." + taxAmount);
            String payTax;
            while (true) {
                System.out.print("Do you want to pay tax? (y/n): ");
                payTax = ValidationUtils.validateInput(scanner.nextLine().toLowerCase(), ValidationUtils.getYesNoPattern());
                if (payTax != null) break;
                System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
            }
            if (payTax.equals("y") || payTax.equals("yes")) {
                taxableAccount.payTax();
                System.out.println("Tax paid successfully!");
                loggingService.logTaxPayment(account, taxAmount);
                return true;
            }
        } else {
            System.out.println("This account is not taxable.");
        }
        return false;
    }

    public boolean processTransfer(Account account, Scanner scanner) {
        if (account instanceof Transferable) {
            Transferable transferableAccount = (Transferable) account;
            String recipientAccNo;
            while (true) {
                System.out.print("Enter recipient account number: ");
                recipientAccNo = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getAccountNumberPattern());
                if (recipientAccNo != null) break;
                System.out.println("Invalid recipient account number format. Please use 3-20 alphanumeric characters.");
            }
            double transferAmount;
            while (true) {
                System.out.print("Enter transfer amount: ");
                String transferInput = scanner.nextLine();
                try {
                    transferAmount = Double.parseDouble(transferInput.trim());
                    if (transferAmount > 0) break;
                } catch (NumberFormatException e) {}
                System.out.println("Invalid transfer amount. Please enter a positive number.");
            }
            Account recipient = new MainAccount(recipientAccNo, "Recipient", 0);
            boolean transferSuccess = transferableAccount.transfer(recipient, transferAmount);
            if (transferSuccess) {
                System.out.println("Transfer successful!");
                loggingService.logTransfer(account, recipient, transferAmount);
                return true;
            } else {
                System.out.println("Transfer failed!");
                return false;
            }
        } else {
            System.out.println("This account does not support transfers.");
            return false;
        }
    }
}