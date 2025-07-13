package com.bankingsystem.model;

public interface Taxable {
    double calculateTax();

    double getTaxRate();

    void payTax();

    String getTaxDetails();
}