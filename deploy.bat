@echo off
setlocal enabledelayedexpansion
echo ==========================================
echo   NBN Mobile Deployment Tool
echo ==========================================

:: 1. Fix JAVA_HOME if invalid or missing
:: Check if currently set JAVA_HOME is valid
set "VALID_JAVA=0"
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" set "VALID_JAVA=1"
)

if "!VALID_JAVA!"=="0" (
    echo [INFO] JAVA_HOME is invalid or not set. Searching for Android Studio JDK...
    if exist "C:\Program Files\Android\Android Studio\jbr" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
        echo [INFO] Using Android Studio JDK: !JAVA_HOME!
    ) else if exist "C:\Program Files\Android\Android Studio\jre" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jre"
        echo [INFO] Using Android Studio JRE: !JAVA_HOME!
    ) else (
        echo [ERROR] No valid Java installation found. 
        echo Please set JAVA_HOME or install Android Studio.
        pause
        exit /b 1
    )
)

:: 2. Attempt to find ADB if not in PATH
where adb >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] ADB not found in PATH. Searching standard locations...
    set "ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
    if not exist "!ADB_PATH!" (
        echo [ERROR] Could not find ADB at !ADB_PATH!
        pause
        exit /b 1
    )
) else (
    set "ADB_PATH=adb"
)

:: Step 1: Check for connected devices
echo [1/3] Checking for connected devices...
"!ADB_PATH!" devices | findstr /v "List of devices attached" | findstr "device" > nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] No device found. Please connect your phone via USB and enable USB Debugging.
    pause
    exit /b 1
)

:: Step 2: Build and Install
echo [2/3] Building and installing APK...
:: We use the JAVA_HOME we just found
call gradlew.bat installDebug
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build or installation failed.
    pause
    exit /b %ERRORLEVEL%
)

:: Step 3: Launch
echo [3/3] Launching app on device...
"!ADB_PATH!" shell am start -n com.example.nothingbutnetmobile/.MainActivity
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] App installed but failed to auto-launch.
) else (
    echo [SUCCESS] App is now running on your device!
)

echo ==========================================
pause
