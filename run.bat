@echo off
REM Compile and Run Script - Chess Game with Console Rules Integration

echo ========================================
echo   Chess Game - Console Rules + GUI
echo ========================================

echo.
echo Compiling Console classes...
javac -cp ".;Console" Console\logic\*.java Console\objects\*.java
if %errorlevel% neq 0 (
    echo Console compilation failed!
    pause
    exit /b 1
)

echo Compiling GUI classes...
javac -cp ".;Console" GUI\*.java
if %errorlevel% neq 0 (
    echo GUI compilation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Compilation Successful!
echo ========================================

echo.
echo Choose run mode:
echo 1. Run GUI Application (Recommended)
echo 2. Exit
echo.
set /p choice="Please enter your choice (1-2): "

if "%choice%"=="1" (
    echo.
    echo Starting GUI Application...
    echo Note: Console will display game logic debug information
    echo.
    java -cp ".;Console;GUI" MainMenuApp
) else if "%choice%"=="2" (
    echo Exiting...
    exit /b 0
) else (
    echo Invalid choice, starting GUI Application...
    java -cp ".;Console;GUI" MainMenuApp
)

echo.
echo Program ended.
pause