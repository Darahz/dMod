@echo off
setlocal
rem JAVA8_HOME can override the local Java 8 installation.
if defined JAVA8_HOME (
    set "JAVA_HOME=%JAVA8_HOME%"
) else (
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.504.1-hotspot"
)
if not exist "%JAVA_HOME%\bin\javac.exe" (
    echo Java 8 JDK not found. Set JAVA8_HOME to your Java 8 JDK directory.
    exit /b 1
)
set "GRADLE_USER_HOME=%~dp0.gradle-user-home"
call "%~dp0gradlew.bat" %*
exit /b %ERRORLEVEL%
