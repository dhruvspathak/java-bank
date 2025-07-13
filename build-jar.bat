@echo off
setlocal enabledelayedexpansion
echo Building Banking System JAR...

REM Clean previous builds
if exist bin rmdir /s /q bin
if exist BankingSystem.jar del BankingSystem.jar

REM Create bin directory
mkdir bin

REM Compile all Java files using for loop
echo Compiling Java files...
for /r "src\main\java" %%f in (*.java) do (
    echo Compiling: %%f
    javac -d bin -cp "src\main\java" "%%f"
    if !ERRORLEVEL! NEQ 0 (
        echo Compilation failed on: %%f
        pause
        exit /b 1
    )
)

REM Create JAR file
echo Creating JAR file...
"%JAVA_HOME%\bin\jar" cfm BankingSystem.jar manifest.txt -C bin .
if %ERRORLEVEL% NEQ 0 (
    echo JAR creation failed. Trying alternative jar command...
    jar cfm BankingSystem.jar manifest.txt -C bin .
    if %ERRORLEVEL% NEQ 0 (
        echo JAR creation failed. Please ensure jar command is available.
        pause
        exit /b 1
    )
)

echo Build complete!
echo To run: java -jar BankingSystem.jar 