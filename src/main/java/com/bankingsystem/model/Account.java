package com.bankingsystem.model;

import com.bankingsystem.util.EncryptionUtils;

public abstract class Account {
    protected String acc_no;
    protected String name;
    protected int amount;
    protected String upi_id;
    protected int credit_card_no = -1;

    protected abstract int getMinBalance();
    protected abstract int getInterestRate();
    public abstract String getAccountId();

    Account(String acc_no, String name, int amount) {
        this.acc_no = acc_no;
        this.name = name;
        this.amount = amount;
        System.out.println("Account created with balance: " + amount);
    }

    Account(String acc_no, String name, int amount, String upi_id) {
        this(acc_no, name, amount);
        this.upi_id = upi_id;
        System.out.println("UPI ID added: " + upi_id);
    }

    Account(String acc_no, String name, int amount, int credit_card_no) {
        this(acc_no, name, amount);
        this.credit_card_no = credit_card_no;
        System.out.println("Credit card added: [ENCRYPTED]");
    }

    Account(String acc_no, String name, int amount, String upi_id, int credit_card_no) {
        this(acc_no, name, amount, upi_id);
        this.credit_card_no = credit_card_no;
        System.out.println("Credit card added: [ENCRYPTED]");
    }

    public int getAmount() {
        return this.amount;
    }

    public String getName() {
        return this.name;
    }

    public int deposit(int amount) {
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive.");
            }
            this.amount += amount;
            System.out.println("Deposited: " + amount + ", New balance: " + this.amount);
        } catch (IllegalArgumentException e) {
            System.err.println("Deposit failed.");
        } catch (Throwable t) {
            System.err.println("An unexpected error occurred during deposit.");
        }
        return this.amount;
    }

    public int withdraw(String upi_id, int amount) {
        try {
            if (upi_id == null || upi_id.trim().isEmpty()) {
                throw new IllegalArgumentException("Provided UPI ID is null or empty.");
            }
            if (this.upi_id == null) {
                throw new IllegalStateException("No UPI ID is associated with this account.");
            }
            if (!this.upi_id.equals(upi_id)) {
                throw new SecurityException("Provided UPI ID does not match the account's UPI ID.");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive.");
            }
            if (this.amount >= amount) {
                this.amount -= amount;
            } else {
                throw new ArithmeticException("Insufficient balance.");
            }
        } catch (IllegalArgumentException | IllegalStateException | SecurityException | ArithmeticException e) {
            System.err.println("Withdrawal failed.");
        } catch (Throwable t) {
            System.err.println("An unexpected error occurred during withdrawal.");
        } finally {
            System.out.println("UPI ID: " + this.upi_id);
        }
        return this.amount;
    }

    public int withdraw(int credit_card_no, int amount) {
        try {
            if (this.credit_card_no != credit_card_no) {
                throw new SecurityException("Provided credit card number does not match the account's credit card.");
            }
            if (credit_card_no < 0) {
                throw new IllegalArgumentException("Credit card number cannot be negative.");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive.");
            }
            if (this.amount >= amount) {
                this.amount -= amount;
            } else {
                throw new ArithmeticException("Insufficient balance.");
            }
        } catch (IllegalArgumentException | ArithmeticException | SecurityException e) {
            System.err.println("Withdrawal failed.");
        } catch (Throwable t) {
            System.err.println("An unexpected error occurred during withdrawal.");
        } finally {
            System.out.println("Credit card number: [ENCRYPTED]");
        }
        return this.amount;
    }

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
            System.err.println("Withdrawal failed.");
        } catch (Throwable t) {
            System.err.println("An unexpected error occurred during withdrawal.");
        }
        return this.amount;
    }

    public void display() {
        System.out.println("Account Details:");
        System.out.println("  Account Number: " + acc_no);
        System.out.println("  Name: " + name);
        System.out.println("  Balance: " + amount);
        if (upi_id != null) {
            System.out.println("  UPI ID: " + upi_id);
        }
        if (credit_card_no != -1) {
            System.out.println("  Credit Card: " + EncryptionUtils.maskCreditCard(credit_card_no));
        }
    }

    @Override
    public String toString() {
        String str = "Account(" + name + ", Balance: ₹" + amount;
        if (upi_id != null)
            str += ", UPI: " + upi_id;
        if (credit_card_no != -1)
            str += ", Card: " + EncryptionUtils.maskCreditCard(credit_card_no);
        return str + ")";
    }
} 