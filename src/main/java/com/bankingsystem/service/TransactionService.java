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
        System.out.print("Enter deposit amount: ");
        int depositAmount = ValidationUtils.validateNumericInput(scanner.nextLine(), 1, Integer.MAX_VALUE);
        if (depositAmount == -1) {
            System.out.println("Invalid deposit amount. Please enter a positive number.");
            return -1;
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
        System.out.print("Enter choice (1-3): ");

        int withdrawChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter withdrawal amount: ");
        int withdrawAmount = ValidationUtils.validateNumericInput(scanner.nextLine(), 1, Integer.MAX_VALUE);
        if (withdrawAmount == -1) {
            System.out.println("Invalid withdrawal amount. Please enter a positive number.");
            return -1;
        }

        int finalBalance = 0;
        String withdrawMethod = "";

        switch (withdrawChoice) {
            case 1:
                finalBalance = account.withdraw(withdrawAmount);
                withdrawMethod = "Simple";
                break;
            case 2:
                System.out.print("Enter UPI ID: ");
                String withdrawUpi = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getUpiPattern());
                if (withdrawUpi == null) {
                    System.out.println("Invalid UPI ID format. Please use format: username@bank");
                    return -1;
                }
                finalBalance = account.withdraw(withdrawUpi, withdrawAmount);
                withdrawMethod = "UPI";
                break;
            case 3:
                System.out.print("Enter credit card number: ");
                int withdrawCard = ValidationUtils.readSecureCreditCard(scanner);
                if (withdrawCard == -1) {
                    return -1;
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
            System.out.println("Tax amount: ₹" + taxAmount);
            System.out.print("Do you want to pay tax? (y/n): ");
            String payTax = ValidationUtils.validateInput(scanner.nextLine().toLowerCase(),
                    ValidationUtils.getYesNoPattern());
            if (payTax == null) {
                System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                return false;
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
            System.out.print("Enter recipient account number: ");
            String recipientAccNo = ValidationUtils.validateInput(scanner.nextLine(),
                    ValidationUtils.getAccountNumberPattern());
            if (recipientAccNo == null) {
                System.out.println("Invalid recipient account number format. Please use 3-20 alphanumeric characters.");
                return false;
            }
            System.out.print("Enter transfer amount: ");
            String transferInput = scanner.nextLine();
            double transferAmount;
            try {
                transferAmount = Double.parseDouble(transferInput.trim());
                if (transferAmount <= 0) {
                    System.out.println("Invalid transfer amount. Please enter a positive number.");
                    return false;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid transfer amount format.");
                return false;
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