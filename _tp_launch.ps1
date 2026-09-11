$ErrorActionPreference = 'Continue'
Set-Location 'C:/Users/Quacky/Documents/Coding/Android Studio/Projects/AlgoLens'
$py = 'C:/Users/Quacky/AppData/Local/Programs/Python/Python311/python.exe'
$tp = '.agents/skills/profilers/android-profiler/bin/trace_processor'
Write-Output "cwd: $((Get-Location))"
try {
    & $py $tp server unix --name algolens1 --daemonize 'cpu-perfetto-20260911T175953.trace' 1> 'tp_srv.log' 2> 'tp_srv.err'
    Write-Output "exit=$LASTEXITCODE"
} catch {
    Write-Output ("EXC: " + $_.Exception.Message)
}
Write-Output '--- tp_srv.log ---'
if (Test-Path 'tp_srv.log') { Get-Content 'tp_srv.log' }
Write-Output '--- tp_srv.err ---'
if (Test-Path 'tp_srv.err') { Get-Content 'tp_srv.err' }