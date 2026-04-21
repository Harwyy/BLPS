@echo off
chcp 65001 > nul
set BASE_URL=http://localhost:8080/blps-0.0.1-SNAPSHOT/api/public/restaurants
set CITY=Москва
set ALL=false

echo ===== GET /api/public/restaurants?city=%CITY%&all=%ALL% =====

curl -s -G "%BASE_URL%" --data-urlencode "city=%CITY%" --data-urlencode "all=%ALL%" -H "Accept: application/json" | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause