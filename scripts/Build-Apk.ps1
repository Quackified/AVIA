[CmdletBinding()]
param(
    [ValidateSet('Debug', 'Release')]
    [string]$Variant = 'Debug',
    [string]$JdkPath
)

$ErrorActionPreference = 'Stop'
$projectDirectory = Split-Path -Parent $PSScriptRoot

if (-not $JdkPath) {
    # Prefer Studio's JDK over a system Java installation (which may be Java 8).
    $programFilesDirectory = [Environment]::GetFolderPath('ProgramFiles')
    $candidates = @(
        (Join-Path $programFilesDirectory 'Android\Android Studio\jbr'),
        $env:JAVA_HOME
    )
    $JdkPath = $candidates | Where-Object {
        $_ -and (Test-Path -LiteralPath (Join-Path $_ 'bin\javac.exe'))
    } | Select-Object -First 1
}
if (-not $JdkPath -or -not (Test-Path -LiteralPath (Join-Path $JdkPath 'bin\javac.exe'))) {
    throw 'A JDK is required. Pass -JdkPath with your Android Studio jbr directory (Java 21).'
}
if ($Variant -eq 'Release' -and -not (Test-Path -LiteralPath (Join-Path $projectDirectory 'keystore.properties'))) {
    throw 'Configure keystore.properties first. See docs/APK_SIGNING.md, or use Android Studio''s signing wizard.'
}

$previousJavaHome = $env:JAVA_HOME
Push-Location -LiteralPath $projectDirectory
try {
    $env:JAVA_HOME = $JdkPath
    & .\gradlew.bat ":app:assemble$Variant" --console=plain
    if ($LASTEXITCODE -ne 0) {
        throw "AVIA $Variant APK build failed (exit code $LASTEXITCODE)."
    }
    $apkDirectory = Join-Path $projectDirectory "app\build\outputs\apk\$($Variant.ToLowerInvariant())"
    Write-Host "AVIA APK output: $apkDirectory"
} finally {
    $env:JAVA_HOME = $previousJavaHome
    Pop-Location
}
