$ErrorActionPreference = 'Continue'
Set-Location 'C:/Users/Quacky/Documents/Coding/Android Studio/Projects/AlgoLens'
$py = 'C:/Users/Quacky/AppData/Local/Programs/Python/Python311/python.exe'
$tp = '.agents/skills/profilers/android-profiler/bin/trace_processor'
& $py $tp query --help 1> 'tp_h.log' 2> 'tp_h.err'
Write-Output '--- help ---'
Get-Content 'tp_h.log' -ErrorAction SilentlyContinue
Get-Content 'tp_h.err' -ErrorAction SilentlyContinue