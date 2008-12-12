@echo off
:setup
@setlocal
if "%JAVA_HOME%" == "" goto noJavaHome

goto ant

:noJavaHome
echo "You must set a JAVA_HOME environment variable"
goto end

:ant
REM .\apache-ant-1.6.2\bin\ant "%1" "%2" "%3" "%4" "%5" "%6" "%7" "%8" "%9"
.\apache-ant-1.6.2\bin\ant %*

:end
@endlocal

