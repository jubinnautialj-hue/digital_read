@echo off
chcp 65001 >nul
title 数字无障碍阅读系统 - 数据库初始化

echo ==============================================
echo   数字无障碍阅读与导航系统 - 数据库初始化
echo ==============================================
echo.

set MYSQL_PATH=%MYSQL_HOME%\bin
set DB_HOST=localhost
set DB_PORT=3306
set DB_USER=root
set DB_PASS=root
set DB_NAME=digital_read

echo [1/2] 检查 MySQL 连接...
if exist "%MYSQL_PATH%\mysql.exe" (
    set MYSQL_CMD="%MYSQL_PATH%\mysql.exe"
) else (
    where mysql >nul 2>&1
    if %errorlevel% equ 0 (
        set MYSQL_CMD=mysql
    ) else (
        echo [错误] 未找到 MySQL，请确保 MySQL 已安装并添加到 PATH 环境变量
        echo 或者设置 MYSQL_HOME 环境变量
        pause
        exit /b 1
    )
)

echo [2/2] 执行数据库脚本...
echo.
echo 正在创建数据库和表结构...
%MYSQL_CMD% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% < "%~dp0..\database\schema.sql"

if %errorlevel% equ 0 (
    echo.
    echo ==============================================
    echo   数据库初始化完成！
    echo ==============================================
    echo.
    echo 数据库名称: %DB_NAME%
    echo 主机: %DB_HOST%:%DB_PORT%
    echo 用户: %DB_USER%
    echo.
    echo 注意：默认用户将在后端首次启动时自动创建
    echo - 管理员: admin / admin123
    echo - 普通用户: user / user123
    echo.
) else (
    echo.
    echo [错误] 数据库初始化失败，请检查 MySQL 服务是否启动
    echo 以及用户名密码是否正确
    echo.
    echo 当前配置:
    echo   主机: %DB_HOST%:%DB_PORT%
    echo   用户: %DB_USER%
    echo   密码: %DB_PASS%
    echo.
    echo 如需修改配置，请编辑 init-database.bat 文件
    echo.
)

pause
