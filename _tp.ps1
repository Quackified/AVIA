# _tp.ps1 <query.sql>  -> runs trace_processor query -f on the trace, logs to tp_q.log / tp_q.err
$ErrorActionPreference = 'Continue'
Set-Location 'C:/Users/Quacky/Documents/Coding/Android Studio/Projects/AlgoLens'
$py = 'C:/Users/Quacky/AppData/Local/Programs/Python/Python311/python.exe'
$tp = '.agents/skills/profilers/android-profiler/bin/trace_processor'
$sqlFile = $args[0]
if ($sqlFile -eq $null) { Write-Output "usage: _tp.ps1 <query.sql>"; exit 1 }
& $py $tp query '-f' $sqlFile 'cpu-perfetto-20260911T175953.trace' 1> 'tp_q.log' 2> 'tp_q.err'
Write-Output "--- tp_q.log ---"
Get-Content 'tp_q.log' -ErrorAction SilentlyContinue
Write-Output "--- tp_q.err ---"
Get-Content 'tp_q.err' -ErrorAction SilentlyContinue