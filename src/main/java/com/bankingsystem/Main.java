package com.bankingsystem;

import com.bankingsystem.model.*;
import com.bankingsystem.service.*;
import com.bankingsystem.util.ValidationUtils;
import java.util.Scanner;
import java.io.File;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        // Security check: Prevent running with elevated privileges
        if (!isRunningAsRoot()) {
            System.err.println("Security Error: Banking application should run with elevated privileges.");
            System.err.println("Please re-run the application.");
            return;
        }

        // Security check: Verify device lock capability
        if (!isDeviceLockable()) {
            System.err.println("Security Warning: Device lock verification failed.");
            System.err.println("Ensure your device supports screen locking for enhanced security.");
        }

        System.out.println("Hello from OOPs class!\n");

        String logPath;
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            if (tempDir == null || tempDir.trim().isEmpty()) {
                tempDir = System.getProperty("user.home") + java.io.File.separator + ".bank_logs";
            }
            logPath = tempDir + java.io.File.separator + "bank_logs_" + System.currentTimeMillis() + ".txt";
        } catch (SecurityException e) {
            System.err.println("Access denied to system properties.");
            logPath = "bank_logs_" + System.currentTimeMillis() + ".txt";
        }

        try (
            LoggingService loggingService = new LoggingService(logPath);
            Scanner scanner = new Scanner(System.in)
        ) {
            AccountService accountService = new AccountService(loggingService);
            TransactionService transactionService = new TransactionService(loggingService);

            System.out.println("=== Banking Account Management System ===\n");

            while (true) {
                System.out.println("Choose operation:");
                System.out.println("1. Create Main Account");
                System.out.println("2. Create Savings Account (requires parent account)");
                System.out.println("3. Create Current Account (requires parent account)");
                System.out.println("4. Exit");
                System.out.print("Enter your choice (1-4): ");
                int choice = -1;
                while (true) {
                    String input = scanner.nextLine();
                    try {
                        choice = Integer.parseInt(input.trim());
                        if (choice >= 1 && choice <= 4) break;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid menu input.");
                    }
                    System.out.print("Invalid choice! Please enter 1-4: ");
                }

                if (choice == 4) {
                    System.out.println("Thank you for using the Banking System!");
                    break;
                }

                if (choice < 1 || choice > 3) {
                    System.out.println("Invalid choice! Please try again.\n");
                    continue;
                }

                Account account = null;
                String accountType = "";

                if (choice == 1) {
                    System.out.println("\n=== Creating Main Account ===");
                    account = createMainAccount(scanner, accountService);
                    accountType = "Main";
                } else {
                    account = createChildAccount(scanner, accountService, choice);
                    accountType = (choice == 2) ? "Savings" : "Current";
                }

                if (account != null) {
                    System.out.println("\nAccount created successfully!");
                    System.out.println("Account ID: " + account.getAccountId());
                    System.out.println("Account Type: " + accountType);
                    accountService.displaySecureAccountInfo(account);

                    showAccountInfo(account);

                    handleTransactions(account, scanner, transactionService);
                }

                System.out.println("\n" + "=".repeat(50) + "\n");
            }

        } catch (Exception e) {
            System.err.println("Error initializing banking system.");
        }
    }

    private static Account createMainAccount(Scanner scanner, AccountService accountService) {
        String accNo;
        while (true) {
            System.out.print("Enter main account number: ");
            accNo = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getAccountNumberPattern());
            if (accNo != null && accNo.length() <= 20) break;
            System.out.println("Invalid account number format. Please use 3-20 alphanumeric characters.");
        }
        String name;
        while (true) {
            System.out.print("Enter account holder name: ");
            name = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getNamePattern());
            if (name != null && name.length() <= 50) break;
            System.out.println("Invalid name format. Please use 2-50 alphabetic characters.");
        }
        int amount;
        while (true) {
            System.out.print("Enter initial balance: ");
            String amtStr = scanner.nextLine();
            try {
                amount = Integer.parseInt(amtStr.trim());
                if (amount >= 0) break;
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount input.");
            }
            System.out.println("Invalid amount. Please enter a positive number.");
        }
        String upiId = getUpiId(scanner);
        if (upiId == null && askYesNo(scanner, "You chose to add a UPI ID but did not provide a valid one. Do you want to try again? (y/n): ")) {
            upiId = getUpiId(scanner);
        }
        int creditCard = getCreditCard(scanner);
        if (creditCard == -2 && askYesNo(scanner, "You chose to add a credit card but did not provide a valid one. Do you want to try again? (y/n): ")) {
            creditCard = getCreditCard(scanner);
        }
        return accountService.createMainAccount(accNo, name, amount, upiId, creditCard);
    }

    private static Account createChildAccount(Scanner scanner, AccountService accountService, int choice) {
        String parentAccNo;
        while (true) {
            System.out.print("Enter parent account number: ");
            parentAccNo = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getAccountNumberPattern());
            if (parentAccNo != null && parentAccNo.length() <= 20) break;
            System.out.println("Invalid parent account number format. Please use 3-20 alphanumeric characters.");
        }
        String name;
        while (true) {
            System.out.print("Enter account holder name: ");
            name = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getNamePattern());
            if (name != null && name.length() <= 50) break;
            System.out.println("Invalid name format. Please use 2-50 alphabetic characters.");
        }
        int amount;
        while (true) {
            System.out.print("Enter initial balance: ");
            String amtStr = scanner.nextLine();
            try {
                amount = Integer.parseInt(amtStr.trim());
                if (amount >= 0) break;
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount input.");
            }
            System.out.println("Invalid amount. Please enter a positive number.");
        }
        String upiId = getUpiId(scanner);
        if (upiId == null && askYesNo(scanner, "You chose to add a UPI ID but did not provide a valid one. Do you want to try again? (y/n): ")) {
            upiId = getUpiId(scanner);
        }
        int creditCard = getCreditCard(scanner);
        if (creditCard == -2 && askYesNo(scanner, "You chose to add a credit card but did not provide a valid one. Do you want to try again? (y/n): ")) {
            creditCard = getCreditCard(scanner);
        }
        if (choice == 2) {
            System.out.println("\n=== Creating Savings Account ===");
            return accountService.createSavingsAccount(parentAccNo, name, amount, upiId, creditCard);
        } else {
            System.out.println("\n=== Creating Current Account ===");
            return accountService.createCurrentAccount(parentAccNo, name, amount, upiId, creditCard);
        }
    }

    private static String getUpiId(Scanner scanner) {
        while (true) {
            System.out.print("Do you want to add UPI ID? (y/n): ");
            String addUpi = ValidationUtils.validateInput(scanner.nextLine().toLowerCase(Locale.ROOT), ValidationUtils.getYesNoPattern());
            if (addUpi == null) {
                System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                continue;
            }
            if (addUpi.equals("y") || addUpi.equals("yes")) {
                System.out.print("Enter UPI ID: ");
                String upiId = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getUpiPattern());
                if (upiId == null || upiId.length() > 50) {
                    System.out.println("Invalid UPI ID format. Please use format: username@bank (max 50 chars)");
                    return null;
                }
                return upiId;
            }
            return null;
        }
    }

    private static int getCreditCard(Scanner scanner) {
        while (true) {
            System.out.print("Do you want to add credit card? (y/n): ");
            String addCard = ValidationUtils.validateInput(scanner.nextLine().toLowerCase(Locale.ROOT), ValidationUtils.getYesNoPattern());
            if (addCard == null) {
                System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                continue;
            }
            if (addCard.equals("y") || addCard.equals("yes")) {
                System.out.print("Enter credit card number: ");
                int card = ValidationUtils.readSecureCreditCard(scanner);
                if (card == -1) {
                    System.out.println("Invalid credit card number. Please try again.");
                    return -2;
                }
                return card;
            }
            return -1;
        }
    }

    private static boolean askYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = ValidationUtils.validateInput(scanner.nextLine().toLowerCase(Locale.ROOT), ValidationUtils.getYesNoPattern());
            if (input == null) {
                System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                continue;
            }
            return input.equals("y") || input.equals("yes");
        }
    }

    private static void showAccountInfo(Account account) {
        if (account instanceof Taxable) {
            Taxable taxableAccount = (Taxable) account;
            System.out.println("\n=== Tax Information ===");
            System.out.println("Tax Rate: " + taxableAccount.getTaxRate() + "%");
            System.out.println("Tax Amount: Rs." + taxableAccount.calculateTax());
            System.out.println("Tax Details: " + taxableAccount.getTaxDetails().replace("₹", "Rs."));
        }

        if (account instanceof Transferable) {
            Transferable transferableAccount = (Transferable) account;
            System.out.println("\n=== Transfer Information ===");
            System.out.println("Transfer Limit: Rs." + transferableAccount.getTransferLimit());
            System.out.println("Transfer Details: " + transferableAccount.getTransferDetails().replace("₹", "Rs."));
        }
    }

    private static void handleTransactions(Account account, Scanner scanner, TransactionService transactionService) {
        while (true) {
            System.out.println("\nTransaction Options:");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Display Account Details");
            System.out.println("4. Pay Tax");
            if (account instanceof Transferable) {
                System.out.println("5. Transfer Money");
            }
            System.out.println("6. Back to Account Creation");
            System.out.print("Enter your choice (1-6): ");
            int transChoice = -1;
            while (true) {
                String input = scanner.nextLine();
                try {
                    transChoice = Integer.parseInt(input.trim());
                    if (transChoice >= 1 && transChoice <= 6) break;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid transaction menu input.");
                }
                System.out.print("Invalid choice! Please enter 1-6: ");
            }

            if (transChoice == 6)
                break;

            switch (transChoice) {
                case 1:
                    transactionService.processDeposit(account, scanner);
                    break;
                case 2:
                    transactionService.processWithdrawal(account, scanner);
                    break;
                case 3:
                    account.display();
                    break;
                case 4:
                    transactionService.processTaxPayment(account, scanner);
                    break;
                case 5:
                    if (account instanceof Transferable) {
                        transactionService.processTransfer(account, scanner);
                    } else {
                        System.out.println("This account does not support transfers.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static boolean isRunningAsRoot() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            return isWindowsAdmin();
        } else {
            // Unix-like systems (Linux/macOS)
            return "root".equals(System.getProperty("user.name"));
        }
    }

    private static boolean isWindowsAdmin() {
        try {
            ProcessBuilder pb = new ProcessBuilder("net", "session");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0; // 'net session' only works as admin
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isDeviceLockable() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            return canLockWindows();
        } else if (os.contains("mac")) {
            return canLockMac();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return canLockLinux();
        } else {
            System.err.println("Unsupported OS for lock check: " + os);
            return false;
        }
    }

    private static boolean canLockWindows() {
        // We check if "rundll32.exe" and "user32.dll" are present (best-effort
        // heuristic).
        try {
            String systemRoot = System.getenv("SystemRoot");
            if (systemRoot == null)
                systemRoot = "C:\\Windows";

            File rundll = new File(systemRoot + "\\System32\\rundll32.exe");
            File user32 = new File(systemRoot + "\\System32\\user32.dll");

            return rundll.exists() && user32.exists();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean canLockLinux() {
        return isCommandAvailable("gnome-screensaver-command") ||
                isCommandAvailable("xdg-screensaver") ||
                isCommandAvailable("loginctl") ||
                isCommandAvailable("xlock") ||
                isCommandAvailable("dm-tool");
    }

    private static boolean canLockMac() {
        return isCommandAvailable("pmset") || isCommandAvailable("osascript");
    }

    private static boolean isCommandAvailable(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("which", command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

}