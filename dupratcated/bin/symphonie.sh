#!/bin/bash
#
#  Copyright 2005 Skiimo, Snowman, Spraynasal, Susmab
#
#  G�nie Logiciel 1 - Universit� de Marne-la-Vall�e.
#
# If jar exists
JAR="symphonie.jar"
if test ! -f "$JAR"; then
 echo "$JAR may not be built. Please run the ant"
 exit 1
fi
# Run application
java -jar $JAR