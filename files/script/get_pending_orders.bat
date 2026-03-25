@echo off
chcp 65001 > nul
set BASE_URL=http://localhost:8080/api/restaurants
set RESTAURANT_ID=8

echo ===== GET /api/restaurants/%RESTAURANT_ID%/orders/pending =====
curl -s "%BASE_URL%/%RESTAURANT_ID%/orders/pending" ^
  -H "Accept: application/json" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause