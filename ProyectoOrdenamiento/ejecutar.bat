@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -encoding UTF-8 -d out src\ordenamiento\*.java
if errorlevel 1 (echo Error de compilacion & pause & exit /b 1)
java -Dfile.encoding=UTF-8 -cp out ordenamiento.Main
pause
