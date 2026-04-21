@echo off
chcp 65001 > nul
set BASE_URL=http://localhost:8080/blps-0.0.1-SNAPSHOT/api/orders

echo ===== POST /api/orders =====
curl -s -X POST "%BASE_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"restaurantId\":9,\"address\":{\"city\":\"Москва\",\"street\":\"Тверская\",\"building\":7,\"latitude\":55.7558,\"longitude\":33.6173,\"floor\":\"2\",\"apartment\":\"12\"},\"items\":[{\"productId\":11,\"quantity\":1},{\"productId\":12,\"quantity\":1},{\"productId\":13,\"quantity\":1}],\"commentToRestaurant\":\"commentToRestaurant\",\"commentToCourier\":\"commentToCourier\",\"leaveAtDoor\":false}" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause