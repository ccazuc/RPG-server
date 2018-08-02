@echo off
cd bin
"C:\Program Files\Java\jdk1.8.0_151\bin\jar.exe" cfm ../createAccount.jar ../createAccount.txt *
cd ..
java -jar -server createAccount.jar
pause