#!/bin/sh
mkdir -p out
javac -encoding UTF-8 -d out src/ordenamiento/*.java || exit 1
java -Dfile.encoding=UTF-8 -cp out ordenamiento.Main
