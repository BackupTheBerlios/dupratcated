@echo off

REM  Copyright 2004-2005 Nathalie Persin, Sebastián Peña Saldarraiga
REM
REM  Interfaces Graphiques - Université de Marne-la-Vallée.

REM Check RSS_HOME exists
if "%RSS_HOME%"=="" goto noHome
if not exist "%RSS_HOME%" goto noHome

REM Check RSSFeeder has been built
if not exist "%RSS_HOME%\lib\RSSFeeder.jar" goto noJar

REM Run application
echo Launching RSSFeeder ...
cd %RSS_HOME%\lib\
java -jar RSSFeeder.jar
goto fin

:noHome
echo RSS_HOME is set incorrectly or not set at all. Please set RSS_HOME.
goto fin

:noJar
echo RSSFeeder application may not be built. Please run the ant command in %RSS_HOME%

:fin
if "%OS%"=="Windows_NT" @endlocal
