package com.bankingsystem.model;

public interface Transferable {
    boolean transfer(Account recipient, double amount);
    double getTransferLimit();
    String getTransferDetails();
} 