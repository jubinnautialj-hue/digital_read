@echo off
chcp 65001 >nul
title 数字无障碍阅读系统 - 一键启动

echo ==============================================
echo   数字无障碍阅读与导航系统 - 一键启动
echo ==============================================
echo.
echo 此脚本将启动以下服务：
echo   [1] 后端服务 (Spring Boot) - 端口 8080
echo   [2] 前端服务 (Vue) - 端口 8081
echo.
echo 请确保 MySQL 数据库已启动
echo ==============================================
echo.

set "SCRIPT_DIR=%~dp0scripts"

echo [步骤 1/3] 检查数据库连接...
echo 请确保 MySQL 服务已启动，数据库配置正确
echo.

echo [步骤 2/3] 启动后端服务...
start "数字无障碍阅读系统 - 后端" cmd /k call "%SCRIPT_DIR%\start-backend.bat"
echo 后端服务正在启动中...
echo.

timeout /t 5 /nobreak >nul

echo [步骤 3/3] 启动前端服务...
start "数字无障碍阅读系统 - 前端" cmd /k call "%SCRIPT_DIR%\start-frontend.bat"
echo 前端服务正在启动中...
echo.

echo ==============================================
echo   启动完成！
echo ==============================================
echo.
echo 服务地址：
echo   前端: http://localhost:8081
echo   后端: http://localhost:8080/api
echo.
echo 默认账号：
echo   管理员: admin / admin123
echo   普通用户: user / user123
echo.
echo 注意：
echo - 首次启动后端需要较长时间（下载 Maven 依赖）
echo - 首次启动前端需要安装 npm 依赖
echo - 请确保 8080 和 8081 端口未被占用
echo.

pause
