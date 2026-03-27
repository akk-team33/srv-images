@echo off
setlocal
rem set "JAVA_HOME=X:\path\to\java\home"
rem set "JAVA=%JAVA_HOME%\bin\java.exe"
set JAVA=java
set "JAR_FILE=template-fat\target\template-fat.jar"
"%JAVA%" -jar "%JAR_FILE%" "%0" %*
endlocal
