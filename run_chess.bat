@echo off
REM Chess Game Startup Script
echo ========================================
echo       Chess Game Starting...
echo ========================================

echo Compiling Java files...
javac -cp ".;Console" Console\logic\*.java Console\objects\*.java GUI\*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful! Starting game...
echo.
java -cp ".;Console;GUI" GUI.MainMenuApp

echo.
echo Game ended.
pause