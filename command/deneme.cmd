echo "lets start"
md ddd
echo created
cd ddd
echo "hi from inside"
cd ..
echo "hi from outside"
rem rmdir /Q /S ddd

echo %time%
timeout 5 >nul
echo %time%
