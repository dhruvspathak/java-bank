@echo off
echo Building Banking System...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files
javac -d bin -cp "src/main/java" src/main/java/com/bankingsystem/**/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo Running Banking System...
    echo.
    java -cp "bin" com.bankingsystem.Main
) else (
    echo Compilation failed!
    pause
) 