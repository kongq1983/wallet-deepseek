# 重启前端：先停后启。

$root = $PSScriptRoot

Write-Host "==> Step 1/2: stop old frontend"
& "$root\stop-frontend.ps1"

Write-Host "==> Step 2/2: start new frontend"
& "$root\start-frontend.ps1"

Write-Host "Frontend restart done. Log: logs/frontend-dev.log"
