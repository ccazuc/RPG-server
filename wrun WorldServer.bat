@echo off
cd bin
"C:\Program Files\Java\jdk1.8.0_102\bin\jar.exe" cfm ../worldserver.jar ../meta.txt *
cd ..
java -jar worldserver.jar
pause