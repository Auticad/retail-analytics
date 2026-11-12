@echo off
rem Compila il progetto ed esegue tutti i test (unitari + integrazione).
rem Richiede: JDK 21 nel PATH (o JAVA_HOME) e Docker avviato per Testcontainers.
cd /d "%~dp0"
echo Build e test in corso: la prima volta Maven scarica le dipendenze, possono servire alcuni minuti...
call mvnw.cmd -B verify > verifica-log.txt 2>&1
if errorlevel 1 (
    echo.
    echo BUILD FALLITA - il dettaglio e' in verifica-log.txt
) else (
    echo.
    echo BUILD OK - compilazione e test superati
)
pause
