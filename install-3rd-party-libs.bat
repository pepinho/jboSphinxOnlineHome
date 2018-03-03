@echo off
set ERRORLEVEL=0
cls

rem so online common
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx.online.common-6.5.3.jar -DgroupId=de.ingmbh.sphinx.online -DartifactId=de.ingmbh.sphinx.online.common -Dversion=6.5.3 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem framework aaa
call mvn install:install-file -Dfile=libs\de.ingmbh.aaa.common-4.0.1.jar -DgroupId=de.ingmbh.aaa -DartifactId=de.ingmbh.aaa.common -Dversion=4.0.1 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem so online adapter common
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx.online.adapter.common-6.5.3.jar -DgroupId=de.ingmbh.sphinx.online -DartifactId=sphinx-online-adapter-common -Dversion=6.5.3 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem so online adapter common tests
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx.online.adapter.common.test-6.5.3-tests.jar -DgroupId=de.ingmbh.sphinx.online -DartifactId=sphinx-online-adapter-common-tests -Dversion=6.5.3 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem so online application
call mvn install:install-file -Dfile=libs\sphinx-online-application-6.5.3.war -DgroupId=de.ingmbh.sphinx.online -DartifactId=sphinx-online-application -Dversion=6.5.3 -Dpackaging=war
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem so api libs
call mvn install:install-file -Dfile=libs\de.ingmbh.framework-de.ingmbh.framework.netlinx-2.5.0.jar -DgroupId=de.ingmbh.framework -DartifactId=de.ingmbh.framework.netlinx -Dversion=2.5.0 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR
call mvn install:install-file -Dfile=libs\de.ingmbh.framework-de.ingmbh.framework.objectpool-2.1.0.jar -DgroupId=de.ingmbh.framework -DartifactId=de.ingmbh.framework.objectpool -Dversion=2.1.0 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx.api2d-de.ingmbh.sphinx.api2d.gal.awt-13.0.1.jar -DgroupId=de.ingmbh.sphinx.api2d -DartifactId=de.ingmbh.sphinx.api2d.gal.awt -Dversion=13.0.1 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx.api2d-de.ingmbh.sphinx.api2d.gal-13.0.1.jar -DgroupId=de.ingmbh.sphinx.api2d -DartifactId=de.ingmbh.sphinx.api2d.gal -Dversion=13.0.1 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx.api2d-de.ingmbh.sphinx.api2d-13.0.1.jar -DgroupId=de.ingmbh.sphinx.api2d -DartifactId=de.ingmbh.sphinx.api2d -Dversion=13.0.1 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx-de.ingmbh.sphinx.common-13.0.1.jar -DgroupId=de.ingmbh.sphinx -DartifactId=de.ingmbh.sphinx.common -Dversion=13.0.1 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR
call mvn install:install-file -Dfile=libs\de.ingmbh.framework.langsupport-de.ingmbh.framework.langsupport-1.1.4.jar -DgroupId=de.ingmbh.framework.langsupport -DartifactId=de.ingmbh.framework.langsupport -Dversion=1.1.4 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR


:NOERROR
echo finished.
goto EXIT



:ERROR
echo mvn returned an error %ERRORLEVEL%
echo we skipped...
goto EXIT

:EXIT
pause