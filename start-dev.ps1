param(
  [switch]$NoDb,
  [switch]$NoBackend,
  [switch]$NoFrontend
)

$root = Split-Path -Parent $MyInvocation.MyCommand.Path

if (-not $NoDb) {
  docker compose -f (Join-Path $root 'docker-compose.yml') up -d
}

function Start-Terminal {
  param(
    [string]$Title,
    [string]$WorkingDirectory,
    [string]$Command
  )

  if (Get-Command wt -ErrorAction SilentlyContinue) {
    Start-Process wt -ArgumentList @(
      '-w','0',
      'new-tab',
      '--title', $Title,
      '-d', $WorkingDirectory,
      'powershell','-NoExit','-Command', $Command
    )
    return
  }

  Start-Process powershell -ArgumentList @('-NoExit','-Command', $Command) -WorkingDirectory $WorkingDirectory
}

if (-not $NoBackend) {
  Start-Terminal -Title 'FinalWorks Backend' -WorkingDirectory (Join-Path $root 'backend') -Command '.\mvnw.cmd spring-boot:run'
}

if (-not $NoFrontend) {
  Start-Terminal -Title 'FinalWorks Frontend' -WorkingDirectory (Join-Path $root 'frontend') -Command 'npm start'
}

Write-Host ''
Write-Host 'If the frontend cannot call the API due to SSL, open this once and accept the certificate:'
Write-Host '  https://localhost:8443/api/final-works'
Write-Host ''
Write-Host 'Frontend:'
Write-Host '  http://localhost:3000'
Write-Host ''
