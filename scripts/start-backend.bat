@echo off
chcp 65001 >nul
title 数字无障碍阅读系统 - 后端服务

echo ==============================================
echo   数字无障碍阅读与导航系统 - 后端服务启动
echo ==============================================
echo.

cd /d "%~dp0..\backend"

echo [检查环境]
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java，请安装 JDK 1.8 或更高版本
    pause
    exit /b 1
)

echo Java 环境正常
echo.

echo [启动后端服务]
echo 正在启动 Spring Boot 应用...
echo.
echo 服务地址: http://localhost:8080/api
echo.
echo 按 Ctrl+C 可停止服务
echo ==============================================
echo.

if exist "mvnw.cmd" (
    echo 使用 Maven Wrapper 启动...
    call mvnw.cmd spring-boot:run
) else (
    where mvn >nul 2>&1
    if %errorlevel% equ 0 (
        echo 使用系统 Maven 启动...
        mvn spring-boot:run
    ) else (
        echo [错误] 未找到 Maven，请安装 Maven 或使用 Maven Wrapper
        echo 请确保已安装 Maven 并添加到 PATH 环境变量
        echo 或者在 backend 目录下执行: mvn -N io.takari:maven:wrapper
        pause
        exit /b 1
    )
)

pause
