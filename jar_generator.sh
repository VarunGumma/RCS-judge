#!/bin/bash
javac Checker.java
jar cvfm RCS.jar manifest.mf *.class
chmod +x RCS.jar
