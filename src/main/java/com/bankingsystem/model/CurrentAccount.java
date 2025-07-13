package com.bankingsystem.model;

public class CurrentAccount extends Account implements Taxable {
    private final int interest_rate = 0;
    private final int min_balance = 0;
    private String current_account_id;
    private String parent_account_id;
    private final double taxRate = 0.12;

    public CurrentAccount(String parent_acc_no, String name, int amount) {
        super(parent_acc_no, name, amount);
        this.parent_account_id = "MAIN" + parent_acc_no;
        this.current_account_id = "CUR" + parent_acc_no;
        System.out.println("Current account created with balance: " + amount);
        System.out.println("Parent Account ID: " + this.parent_account_id);
        System.out.println("Current Account ID: " + this.current_account_id);
    }

    CurrentAccount(String parent_acc_no, String name, int amount, String upi_id) {
        super(parent_acc_no, name, amount, upi_id);
        this.parent_account_id = "MAIN" + parent_acc_no;
        this.current_account_id = "CUR" + parent_acc_no;
        System.out.println("Current account created with balance: " + amount);
        System.out.println("Parent Account ID: " + this.parent_account_id);
        System.out.println("Current Account ID: " + this.current_account_id);
    }

    CurrentAccount(String parent_acc_no, String name, int amount, int credit_card_no) {
        super(parent_acc_no, name, amount, credit_card_no);
        this.parent_account_id = "MAIN" + parent_acc_no;
        this.current_account_id = "CUR" + parent_acc_no;
        System.out.println("Current account created with balance: " + amount);
        System.out.println("Parent Account ID: " + this.parent_account_id);
        System.out.println("Current Account ID: " + this.current_account_id);
    }

    CurrentAccount(String parent_acc_no, String name, int amount, String upi_id, int credit_card_no) {
        super(parent_acc_no, name, amount, upi_id, credit_card_no);
        this.parent_account_id = "MAIN" + parent_acc_no;
        this.current_account_id = "CUR" + parent_acc_no;
        System.out.println("Current account created with balance: " + amount);
        System.out.println("Parent Account ID: " + this.parent_account_id);
        System.out.println("Current Account ID: " + this.current_account_id);
    }

    @Override
    protected int getInterestRate() {
        return this.interest_rate;
    }

    @Override
    protected int getMinBalance() {
        return this.min_balance;
    }

    @Override
    public String getAccountId() {
        return this.current_account_id;
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
        return "Current Account Tax - Rate: " + getTaxRate() + "%, Taxable Amount: ₹" + this.amount;
    }

    @Override
    public int deposit(int amount) {
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive.");
            }
            this.amount += amount;
            System.out.println("Deposited: " + amount + ", New balance: " + this.amount);
        } catch (IllegalArgumentException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        } catch (Throwable t) {
            System.out.println("An unexpected error occurred during deposit: " + t);
        }
        return this.amount;
    }

    @Override
    public int withdraw(int amount) {
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive.");
            }
            if (this.amount >= amount) {
                this.amount -= amount;
                System.out.println("Withdrawn: " + amount + ", New balance: " + this.amount);
            } else {
                throw new ArithmeticException("Insufficient balance.");
            }
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (Throwable t) {
            System.out.println("An unexpected error occurred during withdrawal: " + t);
        }
        return this.amount;
    }
} 