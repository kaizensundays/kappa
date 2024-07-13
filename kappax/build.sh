#!/bin/bash

export JAVA_HOME=$JAVA_17_HOME

gradle clean build -i | tee build.log

echo "Done"

read
