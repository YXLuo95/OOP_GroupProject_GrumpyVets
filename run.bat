@echo off
setlocal ENABLEDELAYEDEXPANSION
REM Chess Game Startup Script - Updated to build into /out and run from there

set OUT=out

echo ========================================
echo     Chess Game - Main Launcher
echo ========================================

REM --- Check JDK availability ---
javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ERROR: javac not found. Please ensure JDK is installed and on PATH.
    pause
    exit /b 1
)

REM --- Clean output directory ---
if exist "%OUT%" (
    rmdir /s /q "%OUT%" >nul 2>&1
)
mkdir "%OUT%" >nul 2>&1

echo.
echo Compiling project to "%OUT%"...
echo   - logic, objects, network, GUI
javac -d "%OUT%" Console\logic\*.java Console\objects\*.java Console\network\*.java GUI\*.java
if %errorlevel% neq 0 (
    echo.
    echo Build failed. See errors above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo       Build Successful!
echo ========================================

echo.
echo Choose how to run the chess game:
echo   1. Start Main Menu (Recommended)
echo   2. Exit
echo.
set /p choice="Please enter your choice (1-2): "

if "%choice%"=="2" (
    echo Exiting...
    exit /b 0
)

echo.
echo Starting Chess Game with Main Menu...
echo You can choose Single Player, Single Player (AI), or Multiplayer from the menu.
echo.
java -cp "%OUT%" GUI.MainMenuApp

echo.
echo Chess Game ended.
pause