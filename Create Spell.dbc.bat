@echo off
cd bin
"C:\Program Files\Java\jdk1.8.0_91\bin\jar.exe" cfm ../createSpellDBC.jar ../SpellDBCMeta.txt *
cd ..
java -jar -server createSpellDBC.jar
pause