@echo off
cd bin
"C:\Program Files\Java\jdk1.8.0_102\bin\jar.exe" cfm ../createItemCacheWDB.jar ../ItemCacheWDBMeta.txt *
cd ..
java -jar -server createItemCacheWDB.jar
pause