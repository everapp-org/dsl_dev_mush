@echo off
REM ============================================================================
REM MCMS Prerequisites Checker for Windows
REM ============================================================================
REM Checks all required software installations, versions, and environment
REM variables needed for MCMS development on Windows.
REM ============================================================================

setlocal enabledelayedexpansion

echo.
echo ============================================================================
echo   MCMS - Mushroom Cultivation Management System
echo   Prerequisites Checker for Windows
echo ============================================================================
echo.

set "PASS_COUNT=0"
set "FAIL_COUNT=0"
set "WARN_COUNT=0"

REM ============================================================================
REM Helper Functions
REM ============================================================================

REM Colors using ANSI escape codes (Windows 10+)
set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "RESET=[0m"

REM ============================================================================
REM 1. Check Java 21
REM ============================================================================

echo %BLUE%[1/7] Checking Java 21...%RESET%
where java >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%i
    echo Found: !JAVA_VERSION!

    echo !JAVA_VERSION! | findstr /i "21\." >nul
    if !ERRORLEVEL! EQU 0 (
        echo %GREEN%✓ PASS%RESET% Java 21 is installed
        set /a PASS_COUNT+=1
    ) else (
        echo %RED%✗ FAIL%RESET% Java 21 required, but different version found
        echo        Download: https://adoptium.net/temurin/releases/?version=21
        set /a FAIL_COUNT+=1
    )
) else (
    echo %RED%✗ FAIL%RESET% Java not found in PATH
    echo        Download: https://adoptium.net/temurin/releases/?version=21
    set /a FAIL_COUNT+=1
)

REM Check JAVA_HOME
echo.
echo Checking JAVA_HOME environment variable...
if defined JAVA_HOME (
    echo Found: JAVA_HOME=%JAVA_HOME%
    if exist "%JAVA_HOME%\bin\java.exe" (
        echo %GREEN%✓ PASS%RESET% JAVA_HOME is set correctly
        set /a PASS_COUNT+=1
    ) else (
        echo %YELLOW%⚠ WARN%RESET% JAVA_HOME is set but java.exe not found at that location
        set /a WARN_COUNT+=1
    )
) else (
    echo %YELLOW%⚠ WARN%RESET% JAVA_HOME not set (recommended but not required)
    echo        Set in: System Properties → Environment Variables
    set /a WARN_COUNT+=1
)

echo.
echo ============================================================================
REM ============================================================================
REM 2. Check Maven 3.9+
REM ============================================================================

echo %BLUE%[2/7] Checking Maven 3.9+...%RESET%
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    for /f "tokens=3" %%i in ('mvn -version ^| findstr /i "Apache Maven"') do set MAVEN_VERSION=%%i
    echo Found: Maven !MAVEN_VERSION!

    REM Check if version is 3.9 or higher
    for /f "tokens=1,2 delims=." %%a in ("!MAVEN_VERSION!") do (
        set MAJ=%%a
        set MIN=%%b
    )
    if !MAJ! GEQ 4 (
        echo %GREEN%✓ PASS%RESET% Maven 3.9+ is installed
        set /a PASS_COUNT+=1
    ) else if !MAJ! EQU 3 (
        if !MIN! GEQ 9 (
            echo %GREEN%✓ PASS%RESET% Maven 3.9+ is installed
            set /a PASS_COUNT+=1
        ) else (
            echo %YELLOW%⚠ WARN%RESET% Maven 3.9+ recommended, found !MAVEN_VERSION!
            echo        Download: https://maven.apache.org/download.cgi
            set /a WARN_COUNT+=1
        )
    )
) else (
    echo %RED%✗ FAIL%RESET% Maven not found in PATH
    echo        Download: https://maven.apache.org/download.cgi
    set /a FAIL_COUNT+=1
)

echo.
echo ============================================================================
REM ============================================================================
REM 3. Check Node.js 18+
REM ============================================================================

echo %BLUE%[3/7] Checking Node.js 18+...%RESET%
where node >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    for /f "tokens=*" %%i in ('node -v') do set NODE_VERSION=%%i
    echo Found: !NODE_VERSION!

    REM Extract major version (remove 'v' prefix)
    set NODE_MAJOR=!NODE_VERSION:v=!
    for /f "tokens=1 delims=." %%a in ("!NODE_MAJOR!") do set NODE_MAJOR=%%a

    if !NODE_MAJOR! GEQ 18 (
        echo %GREEN%✓ PASS%RESET% Node.js 18+ is installed
        set /a PASS_COUNT+=1
    ) else (
        echo %RED%✗ FAIL%RESET% Node.js 18+ required, found !NODE_VERSION!
        echo        Download: https://nodejs.org/
        set /a FAIL_COUNT+=1
    )
) else (
    echo %RED%✗ FAIL%RESET% Node.js not found in PATH
    echo        Download: https://nodejs.org/
    set /a FAIL_COUNT+=1
)

echo.
echo ============================================================================
REM ============================================================================
REM 4. Check npm 9+
REM ============================================================================

echo %BLUE%[4/7] Checking npm 9+...%RESET%
where npm >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    for /f "tokens=*" %%i in ('npm -v') do set NPM_VERSION=%%i
    echo Found: npm !NPM_VERSION!

    REM Extract major version
    for /f "tokens=1 delims=." %%a in ("!NPM_VERSION!") do set NPM_MAJOR=%%a

    if !NPM_MAJOR! GEQ 9 (
        echo %GREEN%✓ PASS%RESET% npm 9+ is installed
        set /a PASS_COUNT+=1
    ) else (
        echo %YELLOW%⚠ WARN%RESET% npm 9+ recommended, found !NPM_VERSION!
        echo        Update: npm install -g npm@latest
        set /a WARN_COUNT+=1
    )
) else (
    echo %RED%✗ FAIL%RESET% npm not found in PATH
    set /a FAIL_COUNT+=1
)

echo.
echo ============================================================================
REM ============================================================================
REM 5. Check JHipster CLI
REM ============================================================================

echo %BLUE%[5/7] Checking JHipster CLI...%RESET%
where jhipster >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    for /f "tokens=*" %%i in ('jhipster --version 2^>^&1') do set JHIPSTER_VERSION=%%i
    echo Found: !JHIPSTER_VERSION!
    echo %GREEN%✓ PASS%RESET% JHipster CLI is installed
    set /a PASS_COUNT+=1
) else (
    echo %RED%✗ FAIL%RESET% JHipster CLI not found
    echo        Install: npm install -g generator-jhipster
    set /a FAIL_COUNT+=1
)

echo.
echo ============================================================================
REM ============================================================================
REM 6. Check Git
REM ============================================================================

echo %BLUE%[6/7] Checking Git...%RESET%
where git >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    for /f "tokens=3" %%i in ('git --version') do set GIT_VERSION=%%i
    echo Found: git version !GIT_VERSION!
    echo %GREEN%✓ PASS%RESET% Git is installed
    set /a PASS_COUNT+=1

    REM Check git configuration
    echo.
    echo Checking Git configuration...
    git config --global user.name >nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        for /f "tokens=*" %%i in ('git config --global user.name') do set GIT_NAME=%%i
        echo   user.name: !GIT_NAME!
    ) else (
        echo %YELLOW%⚠ WARN%RESET% Git user.name not configured
        echo        Set: git config --global user.name "Your Name"
        set /a WARN_COUNT+=1
    )

    git config --global user.email >nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        for /f "tokens=*" %%i in ('git config --global user.email') do set GIT_EMAIL=%%i
        echo   user.email: !GIT_EMAIL!
    ) else (
        echo %YELLOW%⚠ WARN%RESET% Git user.email not configured
        echo        Set: git config --global user.email "you@example.com"
        set /a WARN_COUNT+=1
    )
) else (
    echo %RED%✗ FAIL%RESET% Git not found in PATH
    echo        Download: https://git-scm.com/downloads
    set /a FAIL_COUNT+=1
)

echo.
echo ============================================================================
REM ============================================================================
REM 7. Check Maven Build
REM ============================================================================

echo %BLUE%[7/7] Testing Maven build...%RESET%
if exist pom.xml (
    echo Running: mvn clean compile -q
    mvn clean compile -q >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo %GREEN%✓ PASS%RESET% Maven build successful
        set /a PASS_COUNT+=1
    ) else (
        echo %RED%✗ FAIL%RESET% Maven build failed
        echo        Run manually: mvn clean install
        set /a FAIL_COUNT+=1
    )
) else (
    echo %YELLOW%⚠ WARN%RESET% Not in MCMS project directory (pom.xml not found)
    echo        Navigate to project root and run this script again
    set /a WARN_COUNT+=1
)

echo.
echo ============================================================================
REM ============================================================================
REM Summary
REM ============================================================================

echo.
echo ============================================================================
echo   SUMMARY
echo ============================================================================
echo.
echo   %GREEN%Passed:%RESET%  !PASS_COUNT!
echo   %RED%Failed:%RESET%  !FAIL_COUNT!
echo   %YELLOW%Warnings:%RESET% !WARN_COUNT!
echo.

if !FAIL_COUNT! EQU 0 (
    if !WARN_COUNT! EQU 0 (
        echo %GREEN%✓ ALL CHECKS PASSED%RESET%
        echo.
        echo   Your system is ready for MCMS development!
        echo.
        echo   Next steps:
        echo     1. Read: dev-docs\AGENT_MEMORIES.md
        echo     2. Read: docs\AGENTS.md
        echo     3. Build: mvn clean install
        echo.
    ) else (
        echo %YELLOW%⚠ CHECKS PASSED WITH WARNINGS%RESET%
        echo.
        echo   Your system is mostly ready, but some optional items need attention.
        echo   Review warnings above and fix if needed.
        echo.
    )
) else (
    echo %RED%✗ CHECKS FAILED%RESET%
    echo.
    echo   Please install the missing prerequisites before continuing.
    echo   See: dev-docs\SETUP_GUIDE.md for detailed instructions
    echo.
)

echo ============================================================================
echo.

endlocal
pause
