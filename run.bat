@echo off
REM Chess Game Startup Script - Integrated Version

echo ========================================
echo     Chess Game - Main Launcher
echo ========================================

echo.
echo Compiling all Java files...
echo - Console logic and objects...
javac -cp ".;Console" Console\logic\*.java Console\objects\*.java
if %errorlevel% neq 0 (
    echo Console compilation failed!
    pause
    exit /b 1
)

echo - GUI components...
javac -cp ".;Console" GUI\*.java
if %errorlevel% neq 0 (
    echo GUI compilation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo    Compilation Successful!
echo ========================================

echo.
echo Choose how to run the chess game:
echo 1. Start Chess Game GUI (Recommended)
echo 2. Quick Start (No menu)
echo 3. Exit
echo.
set /p choice="Please enter your choice (1-3): "

if "%choice%"=="1" (
    echo.
    echo Starting Chess Game with Main Menu...
    echo You can choose Single Player or Multiplayer from the menu.
    echo.
    java -cp ".;Console;GUI" GUI.MainMenuApp
) else if "%choice%"=="2" (
    echo.
    echo Quick Starting Chess Game...
    echo Note: Console will display game logic debug information
    echo.
    java -cp ".;Console;GUI" GUI.MainMenuApp
) else if "%choice%"=="3" (
    echo Exiting...
    exit /b 0
) else (
    echo Invalid choice, starting Chess Game with Main Menu...
    java -cp ".;Console;GUI" GUI.MainMenuApp
)

echo.
echo Chess Game ended.
pause