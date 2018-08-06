#!/bin/bash
javac -source 1.7 -target 1.7 -d ./ -cp ./lib/RXTXcomm.jar src/salinometer/*.java
cp -R src/salinometer/resources salinometer
cp manifest_1.txt manifest.txt
jar cfm salinometer.jar manifest.txt salinometer/*.class 
jar vfu salinometer.jar salinometer/resources
if [ -d "dist" ]; then
    rm -r dist
fi
mkdir ./dist
rm -r ./salinometer
mv ./salinometer.jar ./dist
cp -r ./lib ./dist


