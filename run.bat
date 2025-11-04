@echo off
REM 编译并运行脚本 - 编译整个OOP_GroupProject_GrumpyVets项目并启动GUI

echo 编译Console类...
javac -cp ".;Console" Console\logic\*.java Console\objects\*.java
if %errorlevel% neq 0 (
    echo Console编译失败！
    pause
    exit /b 1
)

echo 编译GUI类...
javac -cp ".;Console" GUI\*.java
if %errorlevel% neq 0 (
    echo GUI编译失败！
    pause
    exit /b 1
)

echo 编译成功！

echo 启动GUI应用程序...
java -cp ".;Console;GUI" MainMenuApp