@echo off
cd bin
jar.exe cfm ../worldserver.jar ../ServerMeta.txt *
cd ..
java -jar -server -XX:+UseConcMarkSweepGC worldserver.jar
pause