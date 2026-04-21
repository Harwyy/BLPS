@echo off
chcp 65001 > nul
set BASE_URL=http://localhost:8080/blps-0.0.1-SNAPSHOT/api/orders

echo ===== POST /api/orders =====
curl -s -X POST "%BASE_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"restaurantId\":8,\"items\":[{\"productId\":6,\"quantity\":2}],\"commentToRestaurant\":\"commentToRestaurant\",\"commentToCourier\":\"commentToCourier\",\"leaveAtDoor\":false}" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause