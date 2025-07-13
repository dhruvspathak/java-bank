package com.bankingsystem.model;

public class MainAccount extends Account implements Taxable, Transferable {
    private String main_account_id;
    private final double taxRate = 0.15;
    private final double transferLimit = 1000000;

    public MainAccount(String acc_no, String name, int amount) {
        super(acc_no, name, amount);
        this.main_account_id = "MAIN" + acc_no;
        System.out.println("Main account created with balance: " + amount);
        System.out.println("Main Account ID: " + this.main_account_id);
    }

    MainAccount(String acc_no, String name, int amount, String upi_id) {
        super(acc_no, name, amount, upi_id);
        this.main_account_id = "MAIN" + acc_no;
        System.out.println("Main account created with balance: " + amount);
        System.out.println("Main Account ID: " + this.main_account_id);
    }

    MainAccount(String acc_no, String name, int amount, int credit_card_no) {
        super(acc_no, name, amount, credit_card_no);
        this.main_account_id = "MAIN" + acc_no;
        System.out.println("Main account created with balance: " + amount);
        System.out.println("Main Account ID: " + this.main_account_id);
    }

    MainAccount(String acc_no, String name, int amount, String upi_id, int credit_card_no) {
        super(acc_no, name, amount, upi_id, credit_card_no);
        this.main_account_id = "MAIN" + acc_no;
        System.out.println("Main account created with balance: " + amount);
        System.out.println("Main Account ID: " + this.main_account_id);
    }

    @Override
    protected int getInterestRate() {
        return 0;
    }

    @Override
    protected int getMinBalance() {
        return 0;
    }

    @Override
    public String getAccountId() {
        return this.main_account_id;
    }

    @Override
    public double calculateTax() {
        return this.amount * taxRate;
    }

    @Override
    public double getTaxRate() {
        return taxRate * 100;
    }

    @Override
    public void payTax() {
        double taxAmount = calculateTax();
        if (this.amount >= taxAmount) {
            this.amount -= taxAmount;
            System.out.println("Tax paid: ₹" + taxAmount + ", New balance: ₹" + this.amount);
        } else {
            System.out.println("Insufficient balance to pay tax!");
        }
    }

    @Override
    public String getTaxDetails() {
        return "Main Account Tax - Rate: " + getTaxRate() + "%, Taxable Amount: ₹" + this.amount;
    }

    @Override
    public boolean transfer(Account recipient, double amount) {
        if (amount <= 0) {
            System.out.println("Transfer amount must be positive.");
            return false;
        }
        if (amount > transferLimit) {
            System.out.println("Transfer amount exceeds limit of ₹" + transferLimit);
            return false;
        }
        if (this.amount >= amount) {
            this.amount -= amount;
            recipient.amount += amount;
            System.out.println("Transfer successful: ₹" + amount + " transferred to " + recipient.getAccountId());
            return true;
        } else {
            System.out.println("Insufficient balance for transfer.");
            return false;
        }
    }

    @Override
    public double getTransferLimit() {
        return transferLimit;
    }

    @Override
    public String getTransferDetails() {
        return "Main Account Transfer - Limit: ₹" + transferLimit + ", Current Balance: ₹" + this.amount;
    }
}