#!/bin/bash

export JAVA_HOME=$JAVA_11_HOME

gradle clean build -i | tee build.log

echo "Done"

read
