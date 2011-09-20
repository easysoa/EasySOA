@echo off

rem OW2 FraSCAti distribution
rem Copyright (C) 2008-2009 INRIA, USTL
rem
rem This library is free software; you can redistribute it and/or
rem modify it under the terms of the GNU Lesser General Public
rem License as published by the Free Software Foundation; either
rem version 2 of the License, or (at your option) any later version.
rem
rem This library is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
rem Lesser General Public License for more details.
rem
rem You should have received a copy of the GNU Lesser General Public
rem License along with this library; if not, write to the Free Software
rem Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
rem USA

@setlocal

if defined JAVA_HOME (
  set JAVA=%JAVA_HOME%\bin\java
  set JAVAC=%JAVA_HOME%\bin\javac
  set JAR=%JAVA_HOME%\bin\jar
  set JAVA_OPTS=-Xms16m -Xmx32m
) else (
  echo The JAVA_HOME variable is not set. Please initialize it in your environment
)

if exist "%FRASCATI_HOME%" goto CONFIG

set FRASCATI_HOME=%~dp0
rem Removes the "/bin/" string at the end of this script path
set FRASCATI_HOME=%FRASCATI_HOME:~0,-5%
echo The FRASCATI_HOME variable is not set. Using %FRASCATI_HOME% as default value.

:CONFIG
set FRASCATI_VERSION=1.4
set FRASCATI_LIB=%FRASCATI_HOME%\lib
set FRASCATI_MAIN=org.ow2.frascati.factory.FactoryCommandLine
set FSCRIPT_CONSOLE_MAIN=org.ow2.frascati.fscript.console.Main
set LOGGING=%FRASCATI_HOME%\conf\logging.properties
set LAUNCHER_MAIN=org.ow2.frascati.Launcher
set LAUNCHER_LIB=%FRASCATI_LIB%\frascati-runtime-%FRASCATI_VERSION%.jar
set SCA_APPS_FOLDER=%FRASCATI_HOME%\sca-apps

set FRASCATI_CMD=%1
set PARAMS=%*

rem Test if an option -s is specified or not
rem WARNING: This code can only work if there is one and only one option available for the frascati command

if "%1" == "-version"  goto VERSION
if "%1" == "--version"  goto VERSION
if "%1" == "-v"  goto VERSION   

if "%2" == "-s"  set FRASCATI_SCRIPT_FLAG=TRUE
if "%3" == "-s"  set FRASCATI_SCRIPT_FLAG=TRUE
if "%2" == "-r"  set FRASCATI_REMOTE_FLAG=TRUE
if "%3" == "-r"  set FRASCATI_REMOTE_FLAG=TRUE

if "%1" == "wsdl2java" (
	echo Compiling ...
    goto WSDL2JAVA
) else if "%1" == "compile" (
	echo Compiling ...
    goto COMPILE
) else if "%1" == "run" (
	echo Running OW2 FraSCAti ...
	"%JAVA%" %CUSTOM_JAVA_OPTS% %JAVA_OPTS% -Djava.security.policy="%FRASCATI_HOME%\conf\java.policy" -Djava.util.logging.config.file="%LOGGING%" -Dlog4j.configuration=logging.properties -cp "%FRASCATI_HOME%"\conf;"%LAUNCHER_LIB%" %LAUNCHER_MAIN% %FRASCATI_MAIN% -lib "%FRASCATI_LIB%" %PARAMS:~4%
	echo Exiting OW2 FraSCAti ...
	goto END
) else if "%1" == "explorer" (
	goto EXPLORER
) else if "%1" == "console" (
	echo Running the OW2 FraSCAti FScript console ...
	"%JAVA%" %JAVA_OPTS% -Djava.security.policy="%FRASCATI_HOME%\conf\java.policy" -Djava.util.logging.config.file="%LOGGING%" -Dlog4j.configuration=logging.properties -cp "%FRASCATI_HOME%"\conf;"%LAUNCHER_LIB%" %LAUNCHER_MAIN% %FSCRIPT_CONSOLE_MAIN% -lib "%FRASCATI_LIB%","%FRASCATI_LIB%"\fscript %PARAMS:~4%
	echo Exiting OW2 FraSCAti FScript console ...
	goto END
) else (
	echo "Usage: frascati {wsdl2java|compile|run|explorer|console}"
	echo frascati -?     print this message
	echo frascati -v     OW2 FraSCAti version
	goto END
)

:WSDL2JAVA
set FRASCATI_MAIN=org.ow2.frascati.factory.WebServiceCommandLine
"%JAVA%" -Djava.util.logging.config.file="%LOGGING%" -Dlog4j.configuration=logging.properties -cp "%FRASCATI_HOME%\conf;%LAUNCHER_LIB%" "%LAUNCHER_MAIN%" "%FRASCATI_MAIN%" -lib "%FRASCATI_LIB%" %PARAMS:~4%
goto END

:COMPILE
shift
if "%1"=="" goto COMPILE_USAGE
if "%2"=="" goto COMPILE_USAGE

set CURRENT_DIR=%CD%
set OUTPUT=tmp
mkdir %OUTPUT%

rem Search java sources in .\src\...
setlocal ENABLEDELAYEDEXPANSION
IF ERRORLEVEL 1 (
  echo Impossible d'activer les extensions
  goto END
)
set SRC_FILES=
for /r %1 %%X in (*.java) do (set SRC_FILES=!SRC_FILES! "%%X")
setlocal DISABLEDELAYEDEXPANSION
set SRC_FILES=%SRC_FILES:\=/%
echo %SRC_FILES% > javasrc.tmp~

if ERRORLEVEL 1 (
  echo Cannot find Java source files in %1
  goto END
)

rem Compile Java sources
"%JAVAC%" -d "%OUTPUT%" -cp "%FRASCATI_LIB%\osoa-java-api-2.0.1.2.jar;%FRASCATI_LIB%\jaxb-api-2.1.jar;%FRASCATI_LIB%\geronimo-ws-metadata_2.0_spec-1.1.2.jar;%FRASCATI_LIB%\geronimo-jaxws_2.1_spec-1.0.jar;%FRASCATI_LIB%\jsr311-api-1.1.1.jar;%FRASCATI_LIB%\explorer-1.0.jar;%FRASCATI_LIB%\frascati-explorer-api-%FRASCATI_VERSION%.jar" @javasrc.tmp~
rem Copy composite files
for /r %1 %%X in (*.composite) do (copy /Y "%%X" "%OUTPUT%")
rem Copy Explorer files
mkdir %OUTPUT%\META-INF
for /r %1 %%X in (FraSCAti-Explorer.xml) do (copy /Y "%%X" "%OUTPUT%\META-INF")

rem Build jar file
cd %OUTPUT%
"%JAR%" cf ../%2.jar *
cd %CURRENT_DIR%
echo Library %2.jar created

rem clean up
rmdir /S/Q "%OUTPUT%"
del /Q javasrc.tmp~

goto END

:COMPILE_USAGE
echo Usage: frascati compile [src] [name]
echo [src]  = directory of the sources to compile
echo [name] = name of the jar to build
goto END

:EXPLORER
shift
set EXPLORER_LAUNCHER=org.ow2.frascati.explorer.FrascatiExplorerLauncher
set EXPLORER_CLASSPATH=%FRASCATI_LIB%,%FRASCATI_LIB%\explorer
set EXPLORER_BOOTSTRAP=

echo Running the OW2 FraSCAti Explorer ...

if "%FRASCATI_REMOTE_FLAG%" == "TRUE" (
  set EXPLORER_CLASSPATH=%EXPLORER_CLASSPATH%,%FRASCATI_LIB%\fscript,%FRASCATI_LIB%\remote
  set EXPLORER_BOOTSTRAP="-Dorg.ow2.frascati.bootstrap=org.ow2.frascati.bootstrap.FraSCAtiJDTFractalRest"
  echo -^> FraSCAti Remote plugin activated
  if "%FRASCATI_SCRIPT_FLAG%" == "TRUE" (
    set EXPLORER_CLASSPATH=%EXPLORER_CLASSPATH%,%FRASCATI_LIB%\explorer-fscript-plugin
    echo -^> FraSCAti Script plugin activated
  )
) else (
  if "%FRASCATI_SCRIPT_FLAG%" == "TRUE" (
    set EXPLORER_CLASSPATH=%EXPLORER_CLASSPATH%,%FRASCATI_LIB%\fscript,%FRASCATI_LIB%\explorer-fscript-plugin
    echo -^> FraSCAti Script plugin activated
  )
)

if exist %SCA_APPS_FOLDER% (
  rem update the classpath with user libraries
  pushd "%SCA_APPS_FOLDER%"
  for /d %%X in ("*") do (set EXPLORER_CLASSPATH=%EXPLORER_CLASSPATH%,%%~X)
  popd
  
  rem search for composite files
  setlocal EnableDelayedExpansion
  set COMPOSITES=
  for /r "%SCA_APPS_FOLDER%" %%X in (*.composite) do ( set COMPOSITES=!COMPOSITES! %%~nX )
  setlocal DISABLEDELAYEDEXPANSION
)

"%JAVA%" %JAVA_OPTS% %EXPLORER_BOOTSTRAP% -cp "%LAUNCHER_LIB%" %LAUNCHER_MAIN% %EXPLORER_LAUNCHER% -lib "%EXPLORER_CLASSPATH%" %COMPOSITES%

echo Exiting OW2 FraSCAti Explorer ...
goto END

:VERSION
echo OW2 FraSCAti version %FRASCATI_VERSION%
GOTO END

:END
