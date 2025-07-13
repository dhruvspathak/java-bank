# Banking System - Modular Architecture

This banking system has been refactored into a clean, modular architecture following Java best practices.

## Project Structure

```
src/main/java/com/bankingsystem/
├── model/                    # Data classes and interfaces
│   ├── Taxable.java         # Interface for taxable accounts
│   ├── Transferable.java    # Interface for transferable accounts
│   ├── Account.java         # Abstract base account class
│   ├── MainAccount.java     # Main account implementation
│   ├── SavingsAccount.java  # Savings account implementation
│   └── CurrentAccount.java  # Current account implementation
├── service/                  # Business logic services
│   ├── AccountService.java  # Account creation and management
│   ├── TransactionService.java # Transaction processing
│   └── LoggingService.java  # Logging operations
├── util/                     # Utility classes
│   ├── ValidationUtils.java # Input validation utilities
│   └── EncryptionUtils.java # Encryption and security utilities
├── exception/                # Custom exceptions (future use)
└── Main.java                # Application entry point
```

## Key Benefits of This Architecture

### 1. **Separation of Concerns**
- **Model classes**: Handle data representation and business rules
- **Service classes**: Handle business logic and operations
- **Utility classes**: Provide reusable helper functions
- **Main class**: Orchestrates the application flow

### 2. **Single Responsibility Principle**
Each class has one clear purpose:
- `AccountService`: Account creation and management
- `TransactionService`: Transaction processing
- `LoggingService`: All logging operations
- `ValidationUtils`: Input validation
- `EncryptionUtils`: Security operations

### 3. **Dependency Injection**
Services are injected into other services, making the code:
- More testable (can mock dependencies)
- More flexible (can swap implementations)
- Loosely coupled

### 4. **Improved Maintainability**
- Changes to one component don't affect others
- Easy to find and fix bugs
- Clear code organization

### 5. **Enhanced Security**
- Centralized encryption utilities
- Secure input validation
- Proper exception handling

## How to Run

1. **Compile all classes**:
   ```bash
   javac -cp "src/main/java" src/main/java/com/bankingsystem/**/*.java
   ```

2. **Run the application**:
   ```bash
   java -cp "src/main/java" com.bankingsystem.Main
   ```

## Features

- **Account Types**: Main, Savings, and Current accounts
- **Security**: AES-GCM encryption for credit card data
- **Validation**: Comprehensive input validation
- **Logging**: Secure transaction logging
- **Taxation**: Different tax rates for different account types
- **Transfers**: Money transfer capabilities for main accounts

## Security Features

- **Encryption**: AES-GCM for credit card data
- **Input Validation**: Regex-based validation for all inputs
- **Secure Logging**: Encrypted sensitive data in logs
- **File Permissions**: Secure file creation with proper permissions
- **Side-Channel Protection**: Secure handling of sensitive data

## Future Enhancements

- Database integration
- REST API endpoints
- Web interface
- Advanced security features
- Unit tests
- Integration tests 