echo off

javac -source 1.6 -target 1.6 -d .\ -cp .\lib\RXTXcomm.jar src\salinometer\*.java
mkdir salinometer\resources
xcopy src\salinometer\resources salinometer\resources /E/Y
copy manifest_1.txt manifest.txt
jar cfm salinometer.jar manifest.txt salinometer\*.class 
jar vfu salinometer.jar salinometer\resources

IF EXIST .\dist goto deletedist

:deletedist
del /q /s .\dist  > nul
rmdir /q /s .\dist  > nul
:exit

mkdir .\dist
mkdir .\dist\lib
move /y salinometer.jar .\dist > nul
copy /y .\lib .\dist\lib > nul
del /s /q .\salinometer  > nul
rmdir /s /q .\salinometer  > nul


