@echo off
cd bin
"C:\Program Files\Java\jdk1.8.0_91\bin\jar.exe" cfm ../worldserver.jar ../ServerMeta.txt *
cd ..
java -jar -server -XX:+UseConcMarkSweepGC worldserver.jar
pause