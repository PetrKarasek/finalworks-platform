@ECHO OFF
SETLOCAL

SET "MVNW_PROJECT_DIR=%~dp0"
IF "%MVNW_PROJECT_DIR:~-1%"=="\" SET "MVNW_PROJECT_DIR=%MVNW_PROJECT_DIR:~0,-1%"
SET "WRAPPER_DIR=%MVNW_PROJECT_DIR%\.mvn\wrapper"
SET "PROPS_FILE=%WRAPPER_DIR%\maven-wrapper.properties"
SET "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"

IF NOT EXIST "%PROPS_FILE%" (
  ECHO Maven wrapper properties not found: %PROPS_FILE%
  EXIT /B 1
)

SET "WRAPPER_URL="
FOR /F "usebackq tokens=1,* delims==" %%A IN ("%PROPS_FILE%") DO (
  IF "%%A"=="wrapperUrl" SET "WRAPPER_URL=%%B"
)

IF "%WRAPPER_URL%"=="" (
  SET "WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
)

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Downloading Maven Wrapper JAR...
  POWERSHELL -NoProfile -ExecutionPolicy Bypass -Command "New-Item -ItemType Directory -Force -Path '%WRAPPER_DIR%' | Out-Null; Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
  IF ERRORLEVEL 1 (
    ECHO Failed to download Maven Wrapper JAR from %WRAPPER_URL%
    EXIT /B 1
  )
)

IF NOT "%JAVA_HOME%"=="" (
  SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) ELSE (
  SET "JAVA_EXE=java"
)

"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%MVNW_PROJECT_DIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
SET "ERRORLEVEL_RC=%ERRORLEVEL%"

ENDLOCAL & EXIT /B %ERRORLEVEL_RC%
