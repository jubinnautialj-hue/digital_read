@echo off
chcp 65001 >nul
title 数字无障碍阅读系统 - 前端服务

echo ==============================================
echo   数字无障碍阅读与导航系统 - 前端服务启动
echo ==============================================
echo.

cd /d "%~dp0..\frontend"

echo [检查环境]
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Node.js，请安装 Node.js 14 或更高版本
    pause
    exit /b 1
)

npm -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 npm，请确保 Node.js 安装正确
    pause
    exit /b 1
)

echo Node.js 环境正常
echo.

if not exist "node_modules" (
    echo [安装依赖]
    echo 首次运行需要安装依赖，请稍候...
    echo.
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
    echo 依赖安装完成
    echo.
)

echo [启动前端服务]
echo 正在启动 Vue 开发服务器...
echo.
echo 服务地址: http://localhost:8081
echo.
echo 按 Ctrl+C 可停止服务
echo ==============================================
echo.

call npm run serve

pause
