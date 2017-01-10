@echo off
cd bin
"C:\Program Files\Java\jdk1.8.0_102\bin\jar.exe" cfm ../createAuraDBC.jar ../AuraDBCMeta.txt *
cd ..
java -jar -server createAuraDBC.jar
pause