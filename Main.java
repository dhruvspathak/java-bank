import java.io.*;
import java.util.Scanner;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

// Interface for all accounts - they are all taxable
interface Taxable {
    double calculateTax();

    double getTaxRate();

    void payTax();

    String getTaxDetails();
}

// Interface for parent accounts only - only they can transfer money
interface Transferable {
    boolean transfer(Account recipient, double amount);

    double getTransferLimit();

    String getTransferDetails();
}

public class Main {
    // Encryption key - in production, this should be stored securely and not
    private static final String ENCRYPTION_KEY = "BankSystemSecretKey2024!";
    /**
     * @param creditCardNumber The credit card number to encrypt
     * @return Base64 encoded encrypted string, or "Not set" if no card number
     */
    private static String encryptCreditCard(int creditCardNumber) {
        if (creditCardNumber == -1) {
            return "Not set";
        }

        try {
            // Create a secure key from the encryption key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(hash, "AES");

            // Generate a random initialization vector
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[16];
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Initialize cipher for encryption with CBC mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // Encrypt the credit card number
            String cardNumberStr = String.valueOf(creditCardNumber);
            byte[] encryptedBytes = cipher.doFinal(cardNumberStr.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data for storage
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            // Return Base64 encoded encrypted string
            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            // In case of encryption failure, return a masked version for security
            System.err.println("Encryption failed: " + e.getMessage());
            return maskCreditCard(creditCardNumber);
        }
    }
    /**
     * @param creditCardNumber The credit card number to mask
     * @return Masked credit card string
     */
    public static String maskCreditCard(int creditCardNumber) {
        if (creditCardNumber == -1) {
            return "Not set";
        }

        String cardStr = String.valueOf(creditCardNumber);
        if (cardStr.length() > 4) {
            return "****" + cardStr.substring(cardStr.length() - 4);
        }
        return "****";
    }
    
    /**
     * Validates and sanitizes user input to prevent injection attacks
     * @param input The user input to validate
     * @param pattern The regex pattern to validate against
     * @return Sanitized input or null if invalid
     */
    private static String validateInput(String input, Pattern pattern) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String sanitized = input.trim();
        return pattern.matcher(sanitized).matches() ? sanitized : null;
    }
    
    /**
     * Validates numeric input to prevent injection attacks
     * @param input The numeric input to validate
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return Validated integer or -1 if invalid
     */
    private static int validateNumericInput(String input, int min, int max) {
        try {
            int value = Integer.parseInt(input.trim());
            return (value >= min && value <= max) ? value : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello from OOPs class!\n");

        // Define input validation patterns
        Pattern accountNumberPattern = Pattern.compile("^[A-Za-z0-9]{3,20}$");
        Pattern namePattern = Pattern.compile("^[A-Za-z\\s]{2,50}$");
        Pattern upiPattern = Pattern.compile("^[A-Za-z0-9._-]+@[A-Za-z0-9]+$");
        Pattern yesNoPattern = Pattern.compile("^(y|yes|n|no)$");
        
        Scanner scanner = new Scanner(System.in);

        // === File I/O ===
        String logPath = System.getProperty("user.dir") + File.separator + "logs.txt";
        File file = new File(logPath).getAbsoluteFile();
        
        // Ensure the file has proper permissions and is secure
        if (!file.exists()) {
            try {
                file.createNewFile();
                // Set file permissions to owner read/write only (600 equivalent)
                file.setReadable(false, false); // No read for others
                file.setWritable(false, false); // No write for others
                file.setReadable(true, true);   // Owner can read
                file.setWritable(true, true);   // Owner can write
            } catch (IOException e) {
                System.err.println("Error creating log file: " + e.getMessage());
            }
        }

        try (FileWriter fileWriter = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            System.out.println("=== Banking Account Management System ===\n");

            while (true) {
                System.out.println("Choose operation:");
                System.out.println("1. Create Main Account");
                System.out.println("2. Create Savings Account (requires parent account)");
                System.out.println("3. Create Current Account (requires parent account)");
                System.out.println("4. Exit");
                System.out.print("Enter your choice (1-4): ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

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
                    // Create Main Account
                    System.out.println("\n=== Creating Main Account ===");
                    System.out.print("Enter main account number: ");
                    String accNo = validateInput(scanner.nextLine(), accountNumberPattern);
                    if (accNo == null) {
                        System.out.println("Invalid account number format. Please use 3-20 alphanumeric characters.");
                        continue;
                    }

                    System.out.print("Enter account holder name: ");
                    String name = validateInput(scanner.nextLine(), namePattern);
                    if (name == null) {
                        System.out.println("Invalid name format. Please use 2-50 alphabetic characters.");
                        continue;
                    }

                    System.out.print("Enter initial balance: ");
                    int amount = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    System.out.print("Do you want to add UPI ID? (y/n): ");
                    String addUpi = validateInput(scanner.nextLine().toLowerCase(), yesNoPattern);
                    if (addUpi == null) {
                        System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                        continue;
                    }

                    String upiId = null;
                    if (addUpi.equals("y") || addUpi.equals("yes")) {
                        System.out.print("Enter UPI ID: ");
                        upiId = validateInput(scanner.nextLine(), upiPattern);
                        if (upiId == null) {
                            System.out.println("Invalid UPI ID format. Please use format: username@bank");
                            continue;
                        }
                    }

                    System.out.print("Do you want to add credit card? (y/n): ");
                    String addCard = validateInput(scanner.nextLine().toLowerCase(), yesNoPattern);
                    if (addCard == null) {
                        System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                        continue;
                    }

                    int creditCard = -1;
                    if (addCard.equals("y") || addCard.equals("yes")) {
                        System.out.print("Enter credit card number: ");
                        creditCard = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                    }

                    // Create main account with validated inputs
                    if (upiId != null && creditCard != -1) {
                        account = new MainAccount(accNo, name, amount, upiId, creditCard);
                    } else if (upiId != null) {
                        account = new MainAccount(accNo, name, amount, upiId);
                    } else if (creditCard != -1) {
                        account = new MainAccount(accNo, name, amount, creditCard);
                    } else {
                        account = new MainAccount(accNo, name, amount);
                    }
                    accountType = "Main"; // Main account type

                    // Log main account creation with encrypted credit card
                    String logEntry = String.format(
                            "[%s] %s Account created - ID: %s, Name: %s, Balance: ₹%d, UPI: %s, Card: %s\n",
                            java.time.LocalDateTime.now(),
                            accountType,
                            account.getAccountId(),
                            account.getName(),
                            account.getAmount(),
                            upiId != null ? upiId : "Not set",
                            encryptCreditCard(creditCard));
                    bufferedWriter.write(logEntry);
                    bufferedWriter.flush();

                } else {
                    // Create Savings or Current Account (requires parent account)
                    System.out.print("Enter parent account number: ");
                    String parentAccNo = validateInput(scanner.nextLine(), accountNumberPattern);
                    if (parentAccNo == null) {
                        System.out.println("Invalid parent account number format. Please use 3-20 alphanumeric characters.");
                        continue;
                    }

                    System.out.print("Enter account holder name: ");
                    String name = validateInput(scanner.nextLine(), namePattern);
                    if (name == null) {
                        System.out.println("Invalid name format. Please use 2-50 alphabetic characters.");
                        continue;
                    }

                    System.out.print("Enter initial balance: ");
                    int amount = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    System.out.print("Do you want to add UPI ID? (y/n): ");
                    String addUpi = validateInput(scanner.nextLine().toLowerCase(), yesNoPattern);
                    if (addUpi == null) {
                        System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                        continue;
                    }

                    String upiId = null;
                    if (addUpi.equals("y") || addUpi.equals("yes")) {
                        System.out.print("Enter UPI ID: ");
                        upiId = validateInput(scanner.nextLine(), upiPattern);
                        if (upiId == null) {
                            System.out.println("Invalid UPI ID format. Please use format: username@bank");
                            continue;
                        }
                    }

                    System.out.print("Do you want to add credit card? (y/n): ");
                    String addCard = validateInput(scanner.nextLine().toLowerCase(), yesNoPattern);
                    if (addCard == null) {
                        System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                        continue;
                    }

                    int creditCard = -1;
                    if (addCard.equals("y") || addCard.equals("yes")) {
                        System.out.print("Enter credit card number: ");
                        creditCard = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                    }

                    if (choice == 2) {
                        // Create Savings Account
                        System.out.println("\n=== Creating Savings Account ===");
                        if (upiId != null && creditCard != -1) {
                            account = new SavingsAccount(parentAccNo, name, amount, upiId, creditCard);
                        } else if (upiId != null) {
                            account = new SavingsAccount(parentAccNo, name, amount, upiId);
                        } else if (creditCard != -1) {
                            account = new SavingsAccount(parentAccNo, name, amount, creditCard);
                        } else {
                            account = new SavingsAccount(parentAccNo, name, amount);
                        }
                        accountType = "Savings";
                    } else {
                        // Create Current Account
                        System.out.println("\n=== Creating Current Account ===");
                        if (upiId != null && creditCard != -1) {
                            account = new CurrentAccount(parentAccNo, name, amount, upiId, creditCard);
                        } else if (upiId != null) {
                            account = new CurrentAccount(parentAccNo, name, amount, upiId);
                        } else if (creditCard != -1) {
                            account = new CurrentAccount(parentAccNo, name, amount, creditCard);
                        } else {
                            account = new CurrentAccount(parentAccNo, name, amount, creditCard);
                        }
                        accountType = "Current";
                    }

                    // Log child account creation with encrypted credit card
                    String parentAccNoForLog = account.getAccountId().substring(3); // Remove SAV/CUR prefix
                    String logEntry = String.format(
                            "[%s] %s Account created - Parent: MAIN%s, ID: %s, Name: %s, Balance: ₹%d, UPI: %s, Card: %s\n",
                            java.time.LocalDateTime.now(),
                            accountType,
                            parentAccNoForLog,
                            account.getAccountId(),
                            account.getName(),
                            account.getAmount(),
                            upiId != null ? upiId : "Not set",
                            encryptCreditCard(creditCard));
                    bufferedWriter.write(logEntry);
                    bufferedWriter.flush();
                }

                System.out.println("\nAccount created successfully!");
                System.out.println("Account ID: " + account.getAccountId());
                System.out.println("Account Type: " + accountType);
                account.display();

                // Show tax and transfer information
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

                // Transaction menu
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
                    scanner.nextLine(); // consume newline

                    if (transChoice == 6)
                        break;

                    switch (transChoice) {
                        case 1:
                            System.out.print("Enter deposit amount: ");
                            int depositAmount = scanner.nextInt();
                            scanner.nextLine();
                            int newBalance = account.deposit(depositAmount);

                            // Log transaction
                            String depositLog = String.format(
                                    "[%s] DEPOSIT - Account: %s, Amount: ₹%d, New Balance: ₹%d\n",
                                    java.time.LocalDateTime.now(),
                                    account.getAccountId(),
                                    depositAmount,
                                    newBalance);
                            bufferedWriter.write(depositLog);
                            bufferedWriter.flush();
                            break;

                        case 2:
                            System.out.println("Withdrawal Options:");
                            System.out.println("1. Simple Withdraw");
                            System.out.println("2. Withdraw with UPI");
                            System.out.println("3. Withdraw with Credit Card");
                            System.out.print("Enter choice (1-3): ");

                            int withdrawChoice = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Enter withdrawal amount: ");
                            int withdrawAmount = scanner.nextInt();
                            scanner.nextLine();

                            int finalBalance = 0;
                            String withdrawMethod = "";

                            switch (withdrawChoice) {
                                case 1:
                                    finalBalance = account.withdraw(withdrawAmount);
                                    withdrawMethod = "Simple";
                                    break;
                                case 2:
                                    System.out.print("Enter UPI ID: ");
                                    String withdrawUpi = validateInput(scanner.nextLine(), upiPattern);
                                    if (withdrawUpi == null) {
                                        System.out.println("Invalid UPI ID format. Please use format: username@bank");
                                        continue;
                                    }
                                    finalBalance = account.withdraw(withdrawUpi, withdrawAmount);
                                    withdrawMethod = "UPI";
                                    break;
                                case 3:
                                    System.out.print("Enter credit card number: ");
                                    int withdrawCard = scanner.nextInt();
                                    scanner.nextLine();
                                    finalBalance = account.withdraw(withdrawCard, withdrawAmount);
                                    withdrawMethod = "Credit Card";
                                    break;
                                default:
                                    System.out.println("Invalid choice!");
                                    continue;
                            }

                            // Log transaction
                            String withdrawLog = String.format(
                                    "[%s] WITHDRAW (%s) - Account: %s, Amount: ₹%d, New Balance: ₹%d\n",
                                    java.time.LocalDateTime.now(),
                                    withdrawMethod,
                                    account.getAccountId(),
                                    withdrawAmount,
                                    finalBalance);
                            bufferedWriter.write(withdrawLog);
                            bufferedWriter.flush();
                            break;

                        case 3:
                            account.display();
                            break;

                        case 4:
                            if (account instanceof Taxable) {
                                Taxable taxableAccount = (Taxable) account;
                                double taxAmount = taxableAccount.calculateTax();
                                System.out.println("Tax amount: ₹" + taxAmount);
                                System.out.print("Do you want to pay tax? (y/n): ");
                                String payTax = validateInput(scanner.nextLine().toLowerCase(), yesNoPattern);
                                if (payTax == null) {
                                    System.out.println("Invalid input. Please enter 'y', 'yes', 'n', or 'no'.");
                                    continue;
                                }
                                if (payTax.equals("y") || payTax.equals("yes")) {
                                    taxableAccount.payTax();
                                    System.out.println("Tax paid successfully!");

                                    // Log tax payment
                                    String taxLog = String.format(
                                            "[%s] TAX PAID - Account: %s, Amount: ₹%.2f\n",
                                            java.time.LocalDateTime.now(),
                                            account.getAccountId(),
                                            taxAmount);
                                    bufferedWriter.write(taxLog);
                                    bufferedWriter.flush();
                                }
                            } else {
                                System.out.println("This account is not taxable.");
                            }
                            break;

                        case 5:
                            if (account instanceof Transferable) {
                                Transferable transferableAccount = (Transferable) account;
                                System.out.print("Enter recipient account number: ");
                                String recipientAccNo = validateInput(scanner.nextLine(), accountNumberPattern);
                                if (recipientAccNo == null) {
                                    System.out.println("Invalid recipient account number format. Please use 3-20 alphanumeric characters.");
                                    continue;
                                }
                                System.out.print("Enter transfer amount: ");
                                double transferAmount = scanner.nextDouble();
                                scanner.nextLine();

                                // Create a dummy recipient account for demonstration
                                Account recipient = new MainAccount(recipientAccNo, "Recipient", 0);

                                boolean transferSuccess = transferableAccount.transfer(recipient, transferAmount);
                                if (transferSuccess) {
                                    System.out.println("Transfer successful!");

                                    // Log transfer
                                    String transferLog = String.format(
                                            "[%s] TRANSFER - From: %s, To: %s, Amount: ₹%.2f\n",
                                            java.time.LocalDateTime.now(),
                                            account.getAccountId(),
                                            recipient.getAccountId(),
                                            transferAmount);
                                    bufferedWriter.write(transferLog);
                                    bufferedWriter.flush();
                                } else {
                                    System.out.println("Transfer failed!");
                                }
                            } else {
                                System.out.println("This account does not support transfers.");
                            }
                            break;

                        default:
                            System.out.println("Invalid choice!");
                    }
                }

                System.out.println("\n" + "=".repeat(50) + "\n");
            }

        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

class MainAccount extends Account implements Taxable, Transferable {
    private String main_account_id;
    private final double taxRate = 0.15; // 15% tax rate
    private final double transferLimit = 1000000; // ₹10,00,000 transfer limit

    MainAccount(String acc_no, String name, int amount) {
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
        return 0; // Main account has no interest
    }

    @Override
    protected int getMinBalance() {
        return 0; // Main account has no minimum balance
    }

    @Override
    protected String getAccountId() {
        return this.main_account_id;
    }

    // Taxable interface implementation
    @Override
    public double calculateTax() {
        return this.amount * taxRate;
    }

    @Override
    public double getTaxRate() {
        return taxRate * 100; // Return as percentage
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

    // Transferable interface implementation
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

abstract class Account {
    // protected variables (accessible by child classes)
    protected String acc_no;
    protected String name;
    protected int amount;
    protected String upi_id;
    protected int credit_card_no = -1; // sentinel to indicate "not set"

    // Abstract methods that child classes must implement
    protected abstract int getMinBalance();

    protected abstract int getInterestRate();

    protected abstract String getAccountId();

    // Constructors
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

    // Getters
    public int getAmount() {
        return this.amount;
    }

    public String getName() {
        return this.name;
    }

    // Business Logic
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
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (Throwable t) {
            System.out.println("An unexpected error occurred during withdrawal: " + t);
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
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (Throwable t) {
            System.out.println("An unexpected error occurred during withdrawal: " + t);
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
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (Throwable t) {
            System.out.println("An unexpected error occurred during withdrawal: " + t);
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
            System.out.println("  Credit Card: " + Main.maskCreditCard(credit_card_no));
        }
    }

    // Proper string representation of object
    @Override
    public String toString() {
        String str = "Account(" + name + ", Balance: ₹" + amount;
        if (upi_id != null)
            str += ", UPI: " + upi_id;
        if (credit_card_no != -1)
            str += ", Card: " + Main.maskCreditCard(credit_card_no);
        return str + ")";
    }
}

class SavingsAccount extends Account implements Taxable {
    private final int interest_rate = 2;
    private final int min_balance = 2000;
    private String savings_account_id;
    private String parent_account_id;
    private final double taxRate = 0.10; // 10% tax rate for savings

    SavingsAccount(String parent_acc_no, String name, int amount) {
        super(parent_acc_no, name, amount);
        this.parent_account_id = "MAIN" + parent_acc_no;
        this.savings_account_id = "SAV" + parent_acc_no;
        System.out.println("Savings account created with balance: " + amount);
        System.out.println("Parent Account ID: " + this.parent_account_id);
        System.out.println("Savings Account ID: " + this.savings_account_id);
    }

    SavingsAccount(String parent_acc_no, String name, int amount, String upi_id) {
        super(parent_acc_no, name, amount, upi_id);
        this.parent_account_id = "MAIN" + parent_acc_no;
        this.savings_account_id = "SAV" + parent_acc_no;
        System.out.println("Savings account created with balance: " + amount);
        System.out.println("Parent Account ID: " + this.parent_account_id);
        System.out.println("Savings Account ID: " + this.savings_account_id);
    }

    SavingsAccount(String parent_acc_no, String name, int amount, int credit_card_no) {
        super(parent_acc_no, name, amount, credit_card_no);
        this.parent_account_id = "MAIN" + parent_acc_no;
        this.savings_account_id = "SAV" + parent_acc_no;
        System.out.println("Savings account created with balance: " + amount);
        System.out.println("Parent Account ID: " + this.parent_account_id);
        System.out.println("Savings Account ID: " + this.savings_account_id);
    }

    SavingsAccount(String parent_acc_no, String name, int amount, String upi_id, int credit_card_no) {
        super(parent_acc_no, name, amount, upi_id, credit_card_no);
        this.parent_account_id = "MAIN" + parent_acc_no;
        this.savings_account_id = "SAV" + parent_acc_no;
        System.out.println("Savings account created with balance: " + amount);
        System.out.println("Parent Account ID: " + this.parent_account_id);
        System.out.println("Savings Account ID: " + this.savings_account_id);
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
    protected String getAccountId() {
        return this.savings_account_id;
    }

    // Taxable interface implementation
    @Override
    public double calculateTax() {
        return this.amount * taxRate;
    }

    @Override
    public double getTaxRate() {
        return taxRate * 100; // Return as percentage
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
        return "Savings Account Tax - Rate: " + getTaxRate() + "%, Taxable Amount: ₹" + this.amount;
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

class CurrentAccount extends Account implements Taxable {
    private final int interest_rate = 0;
    private final int min_balance = 0;
    private String current_account_id;
    private String parent_account_id;
    private final double taxRate = 0.12; // 12% tax rate for current accounts

    CurrentAccount(String parent_acc_no, String name, int amount) {
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
    protected String getAccountId() {
        return this.current_account_id;
    }

    // Taxable interface implementation
    @Override
    public double calculateTax() {
        return this.amount * taxRate;
    }

    @Override
    public double getTaxRate() {
        return taxRate * 100; // Return as percentage
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
