@echo off

REM  Copyright 2005 Skiimo, Snowman, Spraynasal, Susmab
REM
REM  Géniel Logiciel 1 - Université de Marne-la-Vallée.

REM Check symphonie.jar has been built
if not exist "symphonie.jar" goto noJar

REM Run application
java -jar symphonie.jar %1
goto fin

:noJar
echo symphonie application may not be built. Please run the ant command in ..

:fin
if "%OS%"=="Windows_NT" @endlocal
