@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM ============================================
REM Ödev Teslim Paketi Hazırlayıcı - Grup 3
REM Windows Versiyonu
REM ============================================

set GROUP_NUMBER=3
set PROJECT_DIR=%~dp0
set SRC_DIR=%PROJECT_DIR%src
set LIB_DIR=%PROJECT_DIR%lib
set OUT_DIR=%PROJECT_DIR%out
set SUBMISSION_DIR=%PROJECT_DIR%submission\%GROUP_NUMBER%

echo ==========================================
echo   Grup %GROUP_NUMBER% - Teslim Paketi Hazırlanıyor
echo ==========================================

REM 1. Submission klasörünü oluştur
if exist "%PROJECT_DIR%submission" rmdir /s /q "%PROJECT_DIR%submission"
mkdir "%SUBMISSION_DIR%"

REM 2. Kaynak kodları derle
echo.
echo [1/5] Kaynak kodlar derleniyor...
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

set JAVAFX_MODULE_PATH=%LIB_DIR%\javafx-base-21.jar;%LIB_DIR%\javafx-base-21-win.jar;%LIB_DIR%\javafx-controls-21.jar;%LIB_DIR%\javafx-controls-21-win.jar;%LIB_DIR%\javafx-fxml-21.jar;%LIB_DIR%\javafx-fxml-21-win.jar;%LIB_DIR%\javafx-graphics-21.jar;%LIB_DIR%\javafx-graphics-21-win.jar

set JUNIT_CLASSPATH=%LIB_DIR%\junit-jupiter-api-5.11.0.jar;%LIB_DIR%\junit-jupiter-engine-5.11.0.jar;%LIB_DIR%\junit-platform-commons-1.11.0.jar;%LIB_DIR%\junit-platform-engine-1.11.0.jar;%LIB_DIR%\junit-platform-launcher-1.11.0.jar;%LIB_DIR%\opentest4j-1.3.0.jar;%LIB_DIR%\apiguardian-api-1.1.2.jar

REM Java dosyalarını bul ve derle
dir /s /b "%SRC_DIR%\*.java" > "%PROJECT_DIR%sources.txt"

javac --module-path "%JAVAFX_MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "%JUNIT_CLASSPATH%" --release 17 -d "%OUT_DIR%" @"%PROJECT_DIR%sources.txt"

if %ERRORLEVEL% neq 0 (
    echo [X] Derleme hatasi!
    del "%PROJECT_DIR%sources.txt"
    pause
    exit /b 1
)
del "%PROJECT_DIR%sources.txt"
echo [OK] Derleme basarili!

REM 3. JAR dosyası oluştur
echo.
echo [2/5] %GROUP_NUMBER%.jar olusturuluyor...

REM Resources klasörünü out'a kopyala
if exist "%SRC_DIR%\resources" xcopy /s /e /i /q "%SRC_DIR%\resources" "%OUT_DIR%\resources"

REM Manifest oluştur
(
echo Manifest-Version: 1.0
echo Main-Class: com.airline.Launcher
echo Class-Path: lib_%GROUP_NUMBER%/javafx-base-21.jar lib_%GROUP_NUMBER%/javafx-controls-21.jar lib_%GROUP_NUMBER%/javafx-fxml-21.jar lib_%GROUP_NUMBER%/javafx-graphics-21.jar
echo.
) > "%PROJECT_DIR%MANIFEST.MF"

cd "%OUT_DIR%"
jar cfm "%SUBMISSION_DIR%\%GROUP_NUMBER%.jar" "%PROJECT_DIR%MANIFEST.MF" .
if %ERRORLEVEL% neq 0 (
    echo [X] JAR olusturma hatasi!
    pause
    exit /b 1
)
del "%PROJECT_DIR%MANIFEST.MF"
cd "%PROJECT_DIR%"
echo [OK] %GROUP_NUMBER%.jar olusturuldu!

REM 4. src_3.zip oluştur
echo.
echo [3/5] src_%GROUP_NUMBER%.zip olusturuluyor...
if exist "%PROJECT_DIR%src_%GROUP_NUMBER%" rmdir /s /q "%PROJECT_DIR%src_%GROUP_NUMBER%"
xcopy /s /e /i /q "%SRC_DIR%" "%PROJECT_DIR%src_%GROUP_NUMBER%"

REM Eclipse .project dosyası
(
echo ^<?xml version="1.0" encoding="UTF-8"?^>
echo ^<projectDescription^>
echo 	^<name^>AirlineReservationSystem_Grup%GROUP_NUMBER%^</name^>
echo 	^<comment^>^</comment^>
echo 	^<projects^>^</projects^>
echo 	^<buildSpec^>
echo 		^<buildCommand^>
echo 			^<name^>org.eclipse.jdt.core.javabuilder^</name^>
echo 			^<arguments^>^</arguments^>
echo 		^</buildCommand^>
echo 	^</buildSpec^>
echo 	^<natures^>
echo 		^<nature^>org.eclipse.jdt.core.javanature^</nature^>
echo 	^</natures^>
echo ^</projectDescription^>
) > "%PROJECT_DIR%src_%GROUP_NUMBER%\.project"

REM Eclipse .classpath dosyası (macOS + Windows JAR'ları)
(
echo ^<?xml version="1.0" encoding="UTF-8"?^>
echo ^<classpath^>
echo 	^<classpathentry kind="src" path=""/^>
echo 	^<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-17"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-base-21.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-base-21-win.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-base-21-mac-aarch64.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-controls-21.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-controls-21-win.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-controls-21-mac-aarch64.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-fxml-21.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-fxml-21-win.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-fxml-21-mac-aarch64.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-graphics-21.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-graphics-21-win.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/javafx-graphics-21-mac-aarch64.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/junit-jupiter-api-5.11.0.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/junit-jupiter-engine-5.11.0.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/junit-platform-commons-1.11.0.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/junit-platform-engine-1.11.0.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/junit-platform-launcher-1.11.0.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/opentest4j-1.3.0.jar"/^>
echo 	^<classpathentry kind="lib" path="../lib_%GROUP_NUMBER%/apiguardian-api-1.1.2.jar"/^>
echo 	^<classpathentry kind="output" path="bin"/^>
echo ^</classpath^>
) > "%PROJECT_DIR%src_%GROUP_NUMBER%\.classpath"

REM Eclipse launch dosyası (Windows)
(
echo ^<?xml version="1.0" encoding="UTF-8" standalone="no"?^>
echo ^<launchConfiguration type="org.eclipse.jdt.launching.localJavaApplication"^>
echo ^<stringAttribute key="org.eclipse.jdt.launching.MAIN_TYPE" value="com.airline.Launcher"/^>
echo ^<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="AirlineReservationSystem_Grup%GROUP_NUMBER%"/^>
echo ^<stringAttribute key="org.eclipse.jdt.launching.VM_ARGUMENTS" value="--module-path ../lib_%GROUP_NUMBER%/javafx-base-21.jar;../lib_%GROUP_NUMBER%/javafx-base-21-win.jar;../lib_%GROUP_NUMBER%/javafx-controls-21.jar;../lib_%GROUP_NUMBER%/javafx-controls-21-win.jar;../lib_%GROUP_NUMBER%/javafx-fxml-21.jar;../lib_%GROUP_NUMBER%/javafx-fxml-21-win.jar;../lib_%GROUP_NUMBER%/javafx-graphics-21.jar;../lib_%GROUP_NUMBER%/javafx-graphics-21-win.jar --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base"/^>
echo ^</launchConfiguration^>
) > "%PROJECT_DIR%src_%GROUP_NUMBER%\RunAirline_Win.launch"

REM Eclipse launch dosyası (macOS)
(
echo ^<?xml version="1.0" encoding="UTF-8" standalone="no"?^>
echo ^<launchConfiguration type="org.eclipse.jdt.launching.localJavaApplication"^>
echo ^<stringAttribute key="org.eclipse.jdt.launching.MAIN_TYPE" value="com.airline.Launcher"/^>
echo ^<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="AirlineReservationSystem_Grup%GROUP_NUMBER%"/^>
echo ^<stringAttribute key="org.eclipse.jdt.launching.VM_ARGUMENTS" value="--module-path ../lib_%GROUP_NUMBER%/javafx-base-21.jar:../lib_%GROUP_NUMBER%/javafx-base-21-mac-aarch64.jar:../lib_%GROUP_NUMBER%/javafx-controls-21.jar:../lib_%GROUP_NUMBER%/javafx-controls-21-mac-aarch64.jar:../lib_%GROUP_NUMBER%/javafx-fxml-21.jar:../lib_%GROUP_NUMBER%/javafx-fxml-21-mac-aarch64.jar:../lib_%GROUP_NUMBER%/javafx-graphics-21.jar:../lib_%GROUP_NUMBER%/javafx-graphics-21-mac-aarch64.jar --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base"/^>
echo ^</launchConfiguration^>
) > "%PROJECT_DIR%src_%GROUP_NUMBER%\RunAirline_Mac.launch"

powershell Compress-Archive -Path "%PROJECT_DIR%src_%GROUP_NUMBER%\*" -DestinationPath "%SUBMISSION_DIR%\src_%GROUP_NUMBER%.zip" -Force
rmdir /s /q "%PROJECT_DIR%src_%GROUP_NUMBER%"
echo [OK] src_%GROUP_NUMBER%.zip olusturuldu!

REM 5. lib_3.zip oluştur
echo.
echo [4/5] lib_%GROUP_NUMBER%.zip olusturuluyor...
if exist "%PROJECT_DIR%lib_%GROUP_NUMBER%" rmdir /s /q "%PROJECT_DIR%lib_%GROUP_NUMBER%"
xcopy /s /e /i /q "%LIB_DIR%" "%PROJECT_DIR%lib_%GROUP_NUMBER%"
powershell Compress-Archive -Path "%PROJECT_DIR%lib_%GROUP_NUMBER%\*" -DestinationPath "%SUBMISSION_DIR%\lib_%GROUP_NUMBER%.zip" -Force
rmdir /s /q "%PROJECT_DIR%lib_%GROUP_NUMBER%"
echo [OK] lib_%GROUP_NUMBER%.zip olusturuldu!

REM 6. report_3.pdf ve video_3.mp4 varsa kopyala
echo.
echo [5/6] Ek dosyalar kontrol ediliyor...
if exist "%PROJECT_DIR%report_%GROUP_NUMBER%.pdf" (
    copy "%PROJECT_DIR%report_%GROUP_NUMBER%.pdf" "%SUBMISSION_DIR%\" >nul
    echo [OK] report_%GROUP_NUMBER%.pdf eklendi!
)
if exist "%PROJECT_DIR%video_%GROUP_NUMBER%.mp4" (
    copy "%PROJECT_DIR%video_%GROUP_NUMBER%.mp4" "%SUBMISSION_DIR%\" >nul
    echo [OK] video_%GROUP_NUMBER%.mp4 eklendi!
)

REM 7. Final 3.zip oluştur
echo.
echo [6/6] Final %GROUP_NUMBER%.zip olusturuluyor...
cd "%PROJECT_DIR%submission"
if exist "%GROUP_NUMBER%.zip" del "%GROUP_NUMBER%.zip"
powershell Compress-Archive -Path "%GROUP_NUMBER%" -DestinationPath "%GROUP_NUMBER%.zip" -Force
cd "%PROJECT_DIR%"
echo [OK] %GROUP_NUMBER%.zip olusturuldu!

echo.
echo ==========================================
echo   TESLIM PAKETI HAZIRLANDI!
echo ==========================================
echo.
echo Final dosya: %PROJECT_DIR%submission\%GROUP_NUMBER%.zip
echo.
echo Eksik dosyalar (ekleyip scripti tekrar calistir):
echo    - report_%GROUP_NUMBER%.pdf
echo    - video_%GROUP_NUMBER%.mp4
echo.
pause
