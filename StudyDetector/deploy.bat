@echo off
chcp 65001 >nul
echo === 学习检测器部署脚本 ===

REM 检查是否在正确的目录
if not exist "settings.gradle" (
    echo 错误：请在StudyDetector项目根目录运行此脚本
    pause
    exit /b 1
)

REM 检查Android SDK环境
if "%ANDROID_HOME%"=="" (
    echo 警告：ANDROID_HOME环境变量未设置
    echo 请确保Android SDK已正确安装
)

REM 清理之前的构建
echo 清理之前的构建...
call gradlew.bat clean

REM 构建Debug版本
echo 构建Debug版本...
call gradlew.bat assembleDebug

REM 检查构建是否成功
if %ERRORLEVEL% EQU 0 (
    echo 构建成功！
    echo APK文件位置：app\build\outputs\apk\debug\app-debug.apk
    
    REM 检查是否有连接的设备
    echo 检查连接的设备...
    adb devices
    
    REM 询问是否自动安装
    set /p choice="是否自动安装到连接的设备？(y/n): "
    if /i "%choice%"=="y" (
        echo 安装APK到设备...
        adb install -r app\build\outputs\apk\debug\app-debug.apk
        
        if %ERRORLEVEL% EQU 0 (
            echo 安装成功！
            echo 请在设备上启动'学习检测器'应用
        ) else (
            echo 安装失败，请手动安装APK文件
        )
    ) else (
        echo 请手动安装APK文件：app\build\outputs\apk\debug\app-debug.apk
    )
) else (
    echo 构建失败，请检查错误信息
    pause
    exit /b 1
)

echo === 部署完成 ===
pause 