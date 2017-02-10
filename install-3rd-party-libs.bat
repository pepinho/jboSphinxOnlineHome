@echo off
rem so online common
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx.online.common-4.0.0.jar -DgroupId=de.ingmbh.sphinx.online -DartifactId=de.ingmbh.sphinx.online.common -Dversion=4.0.0 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem framework aaa
call mvn install:install-file -Dfile=libs\de.ingmbh.aaa.common-1.1.0.jar -DgroupId=de.ingmbh.aaa -DartifactId=de.ingmbh.aaa.common -Dversion=1.1.0 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem so online adapter common
call mvn install:install-file -Dfile=libs\sphinx-online-adapter-common-4.0.0.jar -DgroupId=de.ingmbh.sphinx.online -DartifactId=sphinx-online-adapter-common -Dversion=4.0.0 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem so online adapter common tests
call mvn install:install-file -Dfile=libs\sphinx-online-adapter-common-4.0.0-tests.jar -DgroupId=de.ingmbh.sphinx.online -DartifactId=sphinx-online-adapter-common-tests -Dversion=4.0.0 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem so online application
call mvn install:install-file -Dfile=libs\sphinx-online-application-4.0.0.war -DgroupId=de.ingmbh.sphinx.online -DartifactId=sphinx-online-application -Dversion=4.0.0 -Dpackaging=war
IF %ERRORLEVEL% NEQ 0 goto ERROR

rem so sphinx swing app
call mvn install:install-file -Dfile=libs\de.ingmbh.sphinx.app.swing-4.1.0.jar -DgroupId=de.ingmbh.sphinx.app -DartifactId=de.ingmbh.sphinx.app.swing -Dversion=4.1.0 -Dpackaging=jar
IF %ERRORLEVEL% NEQ 0 (goto ERROR) ELSE goto NOERROR




:ERROR
echo mvn returned an error %ERRORLEVEL%
echo we skipped...
goto EXIT

:NOERROR
echo finished.

:EXIT
pause