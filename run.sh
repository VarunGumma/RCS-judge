#!/bin/bash
set -e
echo "Starting Compilation..."

javac Checker.java
if [ $? -eq 0 ]
then echo "Compilation successful. Attempting to build JAR."
fi

jar cvfm RCS.jar MANIFEST.MF *.class
if [ $? -eq 0 ]
then echo "Added Manifest and testcases successfully."
fi

chmod +x RCS.jar
if [ $? -eq 0 ]
then echo "Executable permissions granted. JAR file ready."
fi

rm *.class
echo "All temp files removed."
set +e
