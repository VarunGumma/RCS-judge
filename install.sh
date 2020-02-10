#!/bin/bash
javac Checker.java
jar cvfm RCS.jar MANIFEST.MF *.class
chmod +x RCS.jar
