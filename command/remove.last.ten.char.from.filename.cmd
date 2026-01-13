setlocal enabledelayedexpansion
@echo off
for /F "delims=" %%I in ('dir /A-D /B *-*.mp4 2^>nul') do (
    echo %%I
	set "filename=%%~nI"
    echo !filename:~0,-10!%%~xI
)