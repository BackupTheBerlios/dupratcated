#!/bin/bash
#
#  Copyright 2004-2005 Skiimo, Snowman, Spraynasal, Susmab
#
#  G�nie Logiciel 1 - Universit� de Marne-la-Vall�e.
#
# If jar exists
JAR="symphonie.jar"
if test ! -f "$JAR"; then
 echo "$JAR may not be built. Please run the ant"
 exit 1
fi
# Build classpath
CPATH="."
cd ../lib
for jarfile in `ls *.jar`
do
 CPATH=$CPATH:$jarfile
done
# Run application
java -cp $CPATH -jar ../bin/$JAR