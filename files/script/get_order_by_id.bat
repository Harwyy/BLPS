@echo off
chcp 65001 > nul
set BASE_URL=http://localhost:8080/api/orders
set ORDER_ID=3

echo ===== GET /api/orders/%ORDER_ID% =====
curl -s "%BASE_URL%/%ORDER_ID%" ^
  -H "Accept: application/json" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause