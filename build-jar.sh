#!/bin/bash
echo "Building Banking System JAR..."

# Clean previous builds
rm -rf bin
rm -f BankingSystem.jar

# Compile all Java files
echo "Compiling Java files..."
javac -d bin src/main/java/com/bankingsystem/*.java src/main/java/com/bankingsystem/*/*.java

# Create JAR file
echo "Creating JAR file..."
jar cfm BankingSystem.jar manifest.txt -C bin .

echo "Build complete!"
echo "To run: java -jar BankingSystem.jar"