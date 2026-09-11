# helper: peek first 16 bytes of the trace + look for interpreter EXEs in common spots
param([string]$tracePath)
if ("$tracePath" -eq "") { $tracePath = 'C:/Users/Quacky/Documents/Coding/Android Studio/Projects/AlgoLens/cpu-perfetto-20260911T175953.trace' }
$f = New-Object System.IO.FileStream($tracePath, 'r')
$b = New-Object byte[] 16
$null = $f.Read($b, 0, 16)
$f.Close()
Write-Output ("HEADER: " + ([System.Text.Encoding]::ASCII.GetString($b)))
Write-Output "---- tool search ----"
foreach ($p in @(
  'C:/Users/Quacky/AppData/Local/Programs/Python',
  'C:/Program Files/Python',
  'C:/Python',
  'C:/Users/Quacky/AppData/Local/JetBrains'
)) {
  if (Test-Path $p) {
    Get-ChildItem -Path $p -Filter 'python.exe' -Recurse -ErrorAction SilentlyContinue | Select-Object -First 3 | ForEach-Object { Write-Output ("PY: " + $_.FullName) }
  }
}
foreach ($n in ('Gradle/bin','Gradle/bin')) {
  $s = Get-ChildItem -Path 'C:/Program Files/Android/android-studio' -Filter 'sqlite*' -Recurse -ErrorAction SilentlyContinue | Select-Object -First 3
  $s | ForEach-Object { Write-Output ("SQLITE: " + $_.FullName) }
}
Write-Output "done"