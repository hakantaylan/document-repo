
@echo off
SET gitcmd=
set repoAddress = https://github.com/hakantaylan/m1.git
FOR /L %%G IN (1,1,10) DO (
    SET gitcmd="git clone https://github.com/hakantaylan/m%%G.git"
    SET gitcmd2=git clone https://github.com/hakantaylan/m%%G.git
    SETLOCAL ENABLEDELAYEDEXPANSION
    echo !gitcmd!
    echo !gitcmd2!
    ENDLOCAL
)