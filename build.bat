@echo off
setlocal enabledelayedexpansion
echo Building Banking System...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files using for loop
for /r "src\main\java" %%f in (*.java) do (
    echo Compiling: %%f
    javac -d bin -cp "src\main\java" "%%f"
    if !ERRORLEVEL! NEQ 0 (
        echo Compilation failed on: %%f
        pause
        exit /b 1
    )
)

echo Compilation successful!
echo.
echo Running Banking System...
echo.
java -cp "bin" com.bankingsystem.Main 