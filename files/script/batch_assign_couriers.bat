@echo off
chcp 65001 > nul
set BASE_URL=http://localhost:8080/blps-0.0.1-SNAPSHOT/api/restaurants
set RESTAURANT_ID=8
set ORDER_IDS=[3,4]

echo ===== POST /api/restaurants/%RESTAURANT_ID%/batch/assign-couriers =====
echo Заказы: %ORDER_IDS%
curl -s -X POST "%BASE_URL%/%RESTAURANT_ID%/batch/assign-couriers" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/json" ^
  -d "{\"orderIds\": %ORDER_IDS%}" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause