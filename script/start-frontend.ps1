# 启动前端（Vite dev server）。优先使用 PATH 中的 pnpm，不硬编码本机路径。

$rootDir = Resolve-Path (Join-Path $PSScriptRoot '..')

# pnpm 通过 cmd /c 启动，避免直接 Start-Process 一个 .ps1/.cmd shim 导致
# “%1 不是有效的 Win32 应用程序” 错误。cmd 会自动解析 PATH 中的 pnpm。

$frontendDir = Join-Path $rootDir 'frontend'
$logDir = Join-Path $rootDir 'logs'
if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir | Out-Null }

$pnpmArgs = 'pnpm --filter frontend-admin-web dev'

Start-Process -FilePath 'cmd.exe' `
  -ArgumentList '/c', $pnpmArgs `
  -WorkingDirectory $frontendDir `
  -RedirectStandardOutput (Join-Path $logDir 'frontend-dev.log') `
  -RedirectStandardError (Join-Path $logDir 'frontend-dev.err') `
  -WindowStyle Hidden

Write-Host "frontend launched (port=5173, cmd /c $pnpmArgs)"
