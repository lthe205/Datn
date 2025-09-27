@echo off
echo ========================================
echo    DATN - Activewear Store
echo    Starting Application...
echo ========================================

echo.
echo Checking Java version...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17+ from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo.
echo Checking if Maven is installed...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven not found. Please install Maven or use IDE to run the project.
    echo Download Maven from: https://maven.apache.org/download.cgi
    echo.
    echo Alternative: Use IDE (IntelliJ IDEA, Eclipse, VS Code)
    echo 1. Open project in IDE
    echo 2. Find DatnApplication.java
    echo 3. Run the main method
    pause
    exit /b 1
)

echo.
echo Starting Spring Boot application...
echo Application will be available at: http://localhost:8080
echo Press Ctrl+C to stop the application
echo.

mvn spring-boot:run

pause
