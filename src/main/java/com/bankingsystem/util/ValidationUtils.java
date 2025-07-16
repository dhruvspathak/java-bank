package com.bankingsystem.util;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^[A-Za-z0-9]{3,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s]{2,50}$");
    private static final Pattern UPI_PATTERN = Pattern.compile("^[A-Za-z0-9._-]+@[A-Za-z0-9._-]+$");
    private static final Pattern YES_NO_PATTERN = Pattern.compile("^(y|yes|n|no)$");

    public static String validateInput(String input, Pattern pattern) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String sanitized = input.trim();
        return pattern.matcher(sanitized).matches() ? sanitized : null;
    }

    public static int validateNumericInput(String input, int min, int max) {
        try {
            int value = Integer.parseInt(input.trim());
            return (value >= min && value <= max) ? value : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static int readSecureCreditCard(Scanner scanner) {
        String input = scanner.nextLine();
        try {
            int cardNumber = Integer.parseInt(input.trim());
            input = null;
            return cardNumber;
        } catch (NumberFormatException e) {
            System.out.println("Invalid credit card number format.");
            return -1;
        }
    }

    public static Pattern getAccountNumberPattern() {
        return ACCOUNT_NUMBER_PATTERN;
    }

    public static Pattern getNamePattern() {
        return NAME_PATTERN;
    }

    public static Pattern getUpiPattern() {
        return UPI_PATTERN;
    }

    public static Pattern getYesNoPattern() {
        return YES_NO_PATTERN;
    }
}