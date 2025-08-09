@echo off
cls
REM Batch file for automatically compiling

set JDKPATH="REPLACE_THIS_WITH_THE_PATH_TO_YOUR_JDK_FOLDER"
cd %~dp0

if %JDKPATH%=="REPLACE_THIS_WITH_THE_PATH_TO_YOUR_JDK_FOLDER" goto :jdkreplace

%JDKPATH%\bin\javac -source 6 -target 6 -bootclasspath %JDKPATH%\jre\lib\rt.jar com\litegpt\*.java
%JDKPATH%\bin\jar cvfe LiteGPT.jar com.litegpt.Main com/litegpt/*.class com/litegpt/*.wav com/litegpt/splash.png 
java -jar LiteGPT.jar
goto :end

:jdkreplace
echo Before you use this script, you need to edit it to use the path to your local Java JDK installation.

:end
pause