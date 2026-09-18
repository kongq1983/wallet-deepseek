# 一键启动前后端：先后端（端口 8080），再前端（端口 5173）。

$root = $PSScriptRoot

Write-Host "==> Step 1/2: start backend"
& "$root\..\backend\script\start-backend.ps1"

Write-Host "==> Step 2/2: start frontend"
& "$root\start-frontend.ps1"

Write-Host "All started. Backend log: backend/logs/mvn-run.log, Frontend log: logs/frontend-dev.log"
