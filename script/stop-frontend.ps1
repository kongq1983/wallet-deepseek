# 停止前端（Vite dev server），默认监听端口 5173。

$port = 5173

try {
    $conns = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction Stop
} catch {
    $conns = @()
}

$pids = $conns | Select-Object -ExpandProperty OwningProcess -Unique

if ($pids.Count -eq 0) {
    Write-Host "No frontend process found on port $port, nothing to stop."
    exit 0
}

foreach ($p in $pids) {
    Write-Host "Stopping frontend process PID=$p ..."
    taskkill /PID $p /F | Out-Null
    if ($?) {
        Write-Host "Stopped PID=$p"
    } else {
        Write-Warning "Failed to stop PID=$p, please check manually."
    }
}

Start-Sleep -Seconds 2
Write-Host "Frontend stopped."
