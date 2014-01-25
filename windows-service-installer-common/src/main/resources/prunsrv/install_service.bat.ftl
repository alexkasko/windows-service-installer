[#ftl encoding="UTF-8"/]
@echo off
rem http://tomcat.apache.org/tomcat-7.0-doc/windows-service-howto.html
if "%OS%" == "Windows_NT" setlocal
set CURRENT_DIR=%cd%
cd /d %~dp0
cd ..
set APP_HOME=%cd%
rem try to remove me
set APP_CLASSPATH=%APP_HOME%\bin\${prunsrvLauncherJarFile}
cd /d "%APP_HOME%\lib"
for %%i in ("*.jar") do call "%APP_HOME%\bin\appendcp.bat" "%APP_HOME%\lib\%%i"
cd /d %APP_HOME%

"%APP_HOME%\bin\prunsrv.exe" //IS//${prunsrvServiceName} ^
--Startup ${prunsrvStartupMode} ^
--StartClass ${prunsrvStartClass} ^
--StartParams ${prunsrvStartParams} ^
--StartPath "%APP_HOME%" ^
--StopClass ${prunsrvStopClass} ^
--StopParams ${prunsrvStopParams} ^
--StartMode jvm ^
--StopMode jvm ^
--StdOutput "%APP_HOME%\${prunsrvStdOutput}" ^
--StdError "%APP_HOME%\${prunsrvStdError}" ^
++JvmOptions -Djava.io.tmpdir="%APP_HOME%\temp";${prunsrvJvmOptions} ^
--JvmMs ${prunsrvJvmMs} ^
--JvmMx ${prunsrvJvmMx} ^
--JvmSs ${prunsrvJvmSs} ^
--DisplayName "${prunsrvDisplayName}" ^
--Description "${prunsrvDescription}" ^
--Jvm "%APP_HOME%\jre\bin\server\jvm.dll" ^
--Classpath "%APP_CLASSPATH%" ^
--StopTimeout ${prunsrvStopTimeout} ^
--LogPath "%APP_HOME%\${prunsrvLogPath}" ^
--LogPrefix ${prunsrvLogPrefix} ^
--LogLevel ${prunsrvLogLevel}

[#if prunsrvStartOnInstrall]
net start ${prunsrvServiceName}
[/#if]

cd %CURRENT_DIR%
