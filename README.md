# Banking Account Management System

A secure, modular Java banking system implementing Object-Oriented Programming principles with enterprise-grade security features.

## 🏦 Business Features

### Account Types
- **Main Account**: Primary account with transfer capabilities and 15% tax rate
- **Savings Account**: Interest-bearing account (2% interest) with 10% tax rate, requires parent account
- **Current Account**: Business account with 0% interest and 12% tax rate, requires parent account

### Banking Operations
- ✅ **Account Creation**: Secure account setup with validation
- ✅ **Deposits**: Simple deposit operations with balance tracking
- ✅ **Withdrawals**: Multiple withdrawal methods (Simple, UPI, Credit Card)
- ✅ **Money Transfers**: Inter-account transfers with limits (₹10,00,000)
- ✅ **Tax Management**: Automatic tax calculation and payment
- ✅ **Transaction Logging**: Comprehensive audit trail for all operations

### Security Features
- 🔐 **AES-GCM Encryption**: Military-grade encryption for credit card data
- 🔐 **Input Validation**: Regex-based validation for all user inputs
- 🔐 **Secure Logging**: Encrypted sensitive data in audit logs
- 🔐 **Access Control**: Package-private constructors with factory methods
- 🔐 **File Security**: Restrictive file permissions for log files

## 🏗️ Technical Architecture

### Modular Design
```
src/main/java/com/bankingsystem/
├── model/                    # Data classes and interfaces
│   ├── Account.java         # Abstract base account class
│   ├── MainAccount.java     # Main account implementation
│   ├── SavingsAccount.java  # Savings account implementation
│   ├── CurrentAccount.java  # Current account implementation
│   ├── Taxable.java         # Interface for taxable accounts
│   └── Transferable.java    # Interface for transferable accounts
├── service/                  # Business logic services
│   ├── AccountService.java  # Account creation and management
│   ├── TransactionService.java # Transaction processing
│   └── LoggingService.java  # Secure logging operations
├── util/                     # Utility classes
│   ├── ValidationUtils.java # Input validation utilities
│   └── EncryptionUtils.java # Encryption and security utilities
└── Main.java                # Application entry point
```

### Design Patterns
- **Factory Pattern**: Controlled object creation for security
- **Dependency Injection**: Service-based architecture
- **Strategy Pattern**: Different account types with common interface
- **Template Method**: Abstract Account class with concrete implementations

### Key Technologies
- **Java 8+**: Core language features
- **AES-GCM**: Advanced encryption standard
- **PBKDF2**: Password-based key derivation
- **Regex Validation**: Input sanitization
- **File I/O**: Secure logging system

## 🔒 Security Implementation

### Encryption Standards
```java
// AES-GCM with 256-bit key
Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
// PBKDF2 with 65,536 iterations
PBEKeySpec spec = new PBEKeySpec(key, salt, 65536, 256);
```

### Access Control
- **Package-Private Constructors**: Prevent direct instantiation
- **Factory Methods**: Controlled object creation
- **Protected Fields**: Encapsulated sensitive data
- **Public Interfaces**: Well-defined API boundaries

### Input Validation
- **Account Numbers**: 3-20 alphanumeric characters
- **Names**: 2-50 alphabetic characters
- **UPI IDs**: username@bank format
- **Credit Cards**: Numeric validation with secure input

### Secure Logging
- **Encrypted Credit Cards**: Never stored in plain text
- **Restricted File Permissions**: Owner-only access
- **Audit Trail**: Complete transaction history
- **Error Handling**: No sensitive information in error messages

## 🚀 Quick Start

### Prerequisites
- Java 8 or higher
- Windows/Linux/macOS

### Environment Setup (Optional)
For production deployment, set these environment variables:
```bash
export BANK_ENCRYPTION_KEY="your-secure-encryption-key"
export BANK_ENCRYPTION_SALT="your-secure-salt"
```

### Build and Run

#### Method 1: Direct Compilation (Development)
```bash
# Windows
./build.bat

# Linux/macOS
chmod +x build.sh
./build.sh
```

#### Method 2: JAR File (Production)
```bash
# Windows
./build-jar.bat

# Linux/macOS
chmod +x build-jar.sh
./build-jar.sh

# Run JAR
java -jar BankingSystem.jar
```

#### Method 3: Manual Compilation
```bash
# Compile
javac -d bin -cp "src/main/java" src/main/java/com/bankingsystem/**/*.java

# Run
java -cp "bin" com.bankingsystem.Main
```

## 📋 Usage Guide

### Creating Accounts
1. **Main Account**: Primary account with full features
2. **Savings Account**: Requires existing main account as parent
3. **Current Account**: Requires existing main account as parent

### Transaction Types
1. **Deposits**: Add funds to any account
2. **Withdrawals**: 
   - Simple withdrawal
   - UPI-based withdrawal (requires UPI ID)
   - Credit card withdrawal (requires card number)
3. **Transfers**: Move money between accounts (Main accounts only)
4. **Tax Payments**: Automatic calculation and payment

### Security Best Practices
- Use strong encryption keys in production
- Regularly rotate encryption keys
- Monitor log files for suspicious activity
- Implement network security for distributed deployment

## 🧪 Testing

### Sample Session
```
=== Banking Account Management System ===

Choose operation:
1. Create Main Account
2. Create Savings Account (requires parent account)
3. Create Current Account (requires parent account)
4. Exit

Enter your choice (1-4): 1

=== Creating Main Account ===
Enter main account number: ACC123
Enter account holder name: John Doe
Enter initial balance: 10000
Do you want to add UPI ID? (y/n): y
Enter UPI ID: john@bank
Do you want to add credit card? (y/n): y
Enter credit card number: 1234567890

Account created successfully!
Account ID: MAINACC123
Account Type: Main
```

## 🔧 Configuration

### Logging Configuration
- **Log Location**: System temp directory or user home
- **Log Format**: Timestamped entries with encrypted sensitive data
- **File Permissions**: Owner-only read/write access

### Security Configuration
- **Encryption**: AES-GCM with PBKDF2 key derivation
- **Key Management**: Environment variables or system properties
- **Fallback**: Development keys (NOT for production)

## 📊 Performance

### System Requirements
- **Memory**: Minimum 128MB RAM
- **Storage**: 1MB for application + log files
- **CPU**: Any modern processor

### Scalability
- **Modular Design**: Easy to extend with new account types
- **Service Layer**: Can be distributed across multiple servers
- **Database Ready**: Architecture supports database integration

## 🛡️ Security Considerations

### Production Deployment
1. **Set Environment Variables**: Use strong encryption keys
2. **Network Security**: Implement SSL/TLS for network communication
3. **Access Control**: Restrict file system access
4. **Monitoring**: Implement intrusion detection
5. **Backup**: Secure backup of log files

### Known Limitations
- **Single-User**: Console-based interface
- **Local Storage**: File-based logging (not distributed)
- **No Database**: In-memory account storage
- **No Network**: Local-only operation

## 🤝 Contributing

### Development Guidelines
1. **Security First**: All changes must maintain security standards
2. **Testing**: Test all new features thoroughly
3. **Documentation**: Update documentation for any changes
4. **Code Review**: Security review required for all changes

### Code Standards
- **Java Conventions**: Follow standard Java naming conventions
- **Error Handling**: Use generic error messages
- **Logging**: Encrypt all sensitive data in logs
- **Validation**: Validate all user inputs

## 📄 License

This project is for educational purposes. For production use, ensure proper security audits and compliance with financial regulations.

## 📞 Support

For issues or questions:
1. Check the security configuration
2. Verify environment variables are set correctly
3. Review log files for error details
4. Ensure Java version compatibility

---

**⚠️ Security Notice**: This system implements banking-grade security features. For production deployment, conduct thorough security audits and ensure compliance with local financial regulations. 