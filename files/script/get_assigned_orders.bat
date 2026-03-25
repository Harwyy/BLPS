@echo off
chcp 65001 > nul
set BASE_URL=http://localhost:8080/api/couriers
set COURIER_ID=1

echo ===== GET /api/couriers/%COURIER_ID%/orders/assigned =====
curl -s "%BASE_URL%/%COURIER_ID%/orders/assigned" ^
  -H "Accept: application/json" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause