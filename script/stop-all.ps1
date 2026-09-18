# 一键停止前后端：先前端（端口 5173），再后端（端口 8080）。

$root = $PSScriptRoot

Write-Host "==> Step 1/2: stop frontend"
& "$root\stop-frontend.ps1"

Write-Host "==> Step 2/2: stop backend"
& "$root\..\backend\script\stop-backend.ps1"

Write-Host "All stopped."
