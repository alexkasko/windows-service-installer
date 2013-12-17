[#ftl encoding="UTF-8"/]
@echo off
rem http://tomcat.apache.org/tomcat-7.0-doc/windows-service-howto.html
if "%OS%" == "Windows_NT" setlocal
set CURRENT_DIR=%cd%
cd /d %~dp0
cd ..
"%cd%\bin\prunsrv.exe" //DS//"${prunsrvServiceName}"
cd %CURRENT_DIR%
