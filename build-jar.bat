@echo off
echo Building Banking System JAR...

REM Clean previous builds
if exist bin rmdir /s /q bin
if exist BankingSystem.jar del BankingSystem.jar

REM Compile all Java files
echo Compiling Java files...
javac -d bin src/main/java/com/bankingsystem/*.java src/main/java/com/bankingsystem/*/*.java

REM Create JAR file
echo Creating JAR file...
jar cfm BankingSystem.jar manifest.txt -C bin .

echo Build complete!
echo To run: java -jar BankingSystem.jar 