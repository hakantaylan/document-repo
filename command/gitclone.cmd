
@echo off
FOR /L %%G IN (1,1,10) DO (
    git clone https://github.com/hakantaylan/m%%G.git
    PING 127.0.0.1 -n 2  >NUL
    echo cloning https://github.com/hakantaylan/m%%G.git has done
)