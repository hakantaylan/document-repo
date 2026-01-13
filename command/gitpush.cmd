@echo off
::echo %1 
if "%1%" == "-s" (echo "Will shutdown after process finishes") 
SET DIR_PREFIX=m
FOR /L %%i IN (1,1,10) DO (
    cd %DIR_PREFIX%%%i
    echo  I am in %DIR_PREFIX%%%i
    git add .
    PING 127.0.0.1 -n 2  >NUL
    git commit -m "aaa"
    PING 127.0.0.1 -n 2  >NUL
    git push
    PING 127.0.0.1 -n 2  >NUL
    echo %DIR_PREFIX%%%i has succesfully pushed to git
    cd ..
)
echo "Process finished........"
if %1% == -s (shutdown /s /f /t 1) 
