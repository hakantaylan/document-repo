@echo off
::echo %1 
::if "%1%" == "-s" (echo "Will shutdown after process finishes") 
SET DIR_PREFIX=m
FOR /L %%i IN (1,1,10) DO (
    cd %DIR_PREFIX%%%i
    echo  I am in %DIR_PREFIX%%%i
    move *.rar .. 2>NUL
    rmdir /s /q .git
    git init
    git add .
    git commit -m "aaa"
    git branch -m main
    git remote add origin https://github.com/hakantaylan/%DIR_PREFIX%%%i.git
    git push -f -u origin main
    PING 127.0.0.1 -n 2  >NUL
    echo %DIR_PREFIX%%%i has succesfully pruned
    cd ..
)
echo "Process finished........"
::if %1% == -s (shutdown /s /f /t 1) 
