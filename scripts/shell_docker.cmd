@echo off
setlocal

set AGENT_APPLICATION=..
set SPRING_PROFILES_ACTIVE=shell,starwars,docker-desktop

call .\support\agent.bat

endlocal