#!/bin/bash

echo "Building Banking System..."

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files
javac -d bin -cp "src/main/java" src/main/java/com/bankingsystem/**/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo
    echo "Running Banking System..."
    echo
    java -cp "bin" com.bankingsystem.Main
else
    echo "Compilation failed!"
    exit 1
fi 