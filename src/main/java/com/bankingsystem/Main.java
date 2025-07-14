package com.bankingsystem;

import com.bankingsystem.model.*;
import com.bankingsystem.service.*;
import com.bankingsystem.util.ValidationUtils;
import java.util.Scanner;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Security check: Prevent running with elevated privileges
        if (!isRunningAsRoot()) {
            System.err.println("Security Error: Banking application should run with elevated privileges.");
            System.err.println("Please re-run the application.");
            System.exit(1);
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
            System.err.println("Access denied to system properties: " + e.getMessage());
            logPath = "bank_logs_" + System.currentTimeMillis() + ".txt";
        }

        try (LoggingService loggingService = new LoggingService(logPath)) {
            AccountService accountService = new AccountService(loggingService);
            TransactionService transactionService = new TransactionService(loggingService);
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== Banking Account Management System ===\n");

            while (true) {
                System.out.println("Choose operation:");
                System.out.println("1. Create Main Account");
                System.out.println("2. Create Savings Account (requires parent account)");
                System.out.println("3. Create Current Account (requires parent account)");
                System.out.println("4. Exit");
                System.out.print("Enter your choice (1-4): ");

                int choice = scanner.nextInt();
                scanner.nextLine();

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
            System.err.println("Error initializing banking system: " + e.getMessage());
        }
    }

    private static Account createMainAccount(Scanner scanner, AccountService accountService) {
        System.out.print("Enter main account number: ");
        String accNo = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getAccountNumberPattern());
        if (accNo == null) {
            System.out.println("Invalid account number format. Please use 3-20 alphanumeric characters.");
            return null;
        }

        System.out.print("Enter account holder name: ");
        String name = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getNamePattern());
        if (name == null) {
            System.out.println("Invalid name format. Please use 2-50 alphabetic characters.");
            return null;
        }
        if (name.length() > 50) {
            System.out.println("Account holder name too long. Maximum 50 characters allowed.");
            return null;
        }

        System.out.print("Enter initial balance: ");
        int amount = scanner.nextInt();
        scanner.nextLine();

        String upiId = getUpiId(scanner);
        int creditCard = getCreditCard(scanner);

        return accountService.createMainAccount(accNo, name, amount, upiId, creditCard);
    }

    private static Account createChildAccount(Scanner scanner, AccountService accountService, int choice) {
        System.out.print("Enter parent account number: ");
        String parentAccNo = ValidationUtils.validateInput(scanner.nextLine(),
                ValidationUtils.getAccountNumberPattern());
        if (parentAccNo == null) {
            System.out.println("Invalid parent account number format. Please use 3-20 alphanumeric characters.");
            return null;
        }
        if (parentAccNo.length() > 20) {
            System.out.println("Parent account number too long. Maximum 20 characters allowed.");
            return null;
        }

        System.out.print("Enter account holder name: ");
        String name = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getNamePattern());
        if (name == null) {
            System.out.println("Invalid name format. Please use 2-50 alphabetic characters.");
            return null;
        }
        if (name.length() > 50) {
            System.out.println("Account holder name too long. Maximum 50 characters allowed.");
            return null;
        }

        System.out.print("Enter initial balance: ");
        int amount = scanner.nextInt();
        scanner.nextLine();

        String upiId = getUpiId(scanner);
        int creditCard = getCreditCard(scanner);

        if (choice == 2) {
            System.out.println("\n=== Creating Savings Account ===");
            return accountService.createSavingsAccount(parentAccNo, name, amount, upiId, creditCard);
        } else {
            System.out.println("\n=== Creating Current Account ===");
            return accountService.createCurrentAccount(parentAccNo, name, amount, upiId, creditCard);
        }
    }

    private static String getUpiId(Scanner scanner) {
        System.out.print("Do you want to add UPI ID? (y/n): ");
        String addUpi = ValidationUtils.validateInput(scanner.nextLine().toLowerCase(),
                ValidationUtils.getYesNoPattern());
        if (addUpi == null) {
            System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
            return null;
        }

        if (addUpi.equals("y") || addUpi.equals("yes")) {
            System.out.print("Enter UPI ID: ");
            String upiId = ValidationUtils.validateInput(scanner.nextLine(), ValidationUtils.getUpiPattern());
            if (upiId == null) {
                System.out.println("Invalid UPI ID format. Please use format: username@bank");
                return null;
            }
            if (upiId.length() > 50) {
                System.out.println("UPI ID too long. Maximum 50 characters allowed.");
                return null;
            }
            return upiId;
        }
        return null;
    }

    private static int getCreditCard(Scanner scanner) {
        System.out.print("Do you want to add credit card? (y/n): ");
        String addCard = ValidationUtils.validateInput(scanner.nextLine().toLowerCase(),
                ValidationUtils.getYesNoPattern());
        if (addCard == null) {
            System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
            return -1;
        }

        if (addCard.equals("y") || addCard.equals("yes")) {
            System.out.print("Enter credit card number: ");
            return ValidationUtils.readSecureCreditCard(scanner);
        }
        return -1;
    }

    private static void showAccountInfo(Account account) {
        if (account instanceof Taxable) {
            Taxable taxableAccount = (Taxable) account;
            System.out.println("\n=== Tax Information ===");
            System.out.println("Tax Rate: " + taxableAccount.getTaxRate() + "%");
            System.out.println("Tax Amount: ₹" + taxableAccount.calculateTax());
            System.out.println("Tax Details: " + taxableAccount.getTaxDetails());
        }

        if (account instanceof Transferable) {
            Transferable transferableAccount = (Transferable) account;
            System.out.println("\n=== Transfer Information ===");
            System.out.println("Transfer Limit: ₹" + transferableAccount.getTransferLimit());
            System.out.println("Transfer Details: " + transferableAccount.getTransferDetails());
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

            int transChoice = scanner.nextInt();
            scanner.nextLine();

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
        String os = System.getProperty("os.name").toLowerCase();

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
        String os = System.getProperty("os.name").toLowerCase();

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