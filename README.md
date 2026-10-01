# AVIA

An Android app for exploring algorithms through interactive visualizations,
source-code traces, practice questions, and step prediction. Built with Kotlin
and Jetpack Compose, with Firebase integration for account features.

## Build locally

1. Open the repository in Android Studio and let Gradle sync.
2. Use Java 21 for Gradle. Android Studio's bundled `jbr-21` is suitable.
3. Install Android SDK platform 37 and Build Tools 37 through SDK Manager.
4. Register an Android app with package name `com.avia` in your Firebase project
   and download its client configuration to `app/google-services.json`.
   This machine-specific configuration is excluded from Git.
5. Build a debug APK with the Gradle wrapper:

   ```powershell
   $env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
   .\gradlew.bat :app:assembleDebug
   ```

On Windows, the helper also selects the installed Studio JDK:

```powershell
.\scripts\Build-Apk.ps1 -Variant Debug
```

The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Signing

Copy `keystore.properties.example` to `keystore.properties` and enter your
keystore path, alias, and passwords locally. Then run:

```powershell
.\scripts\Build-Apk.ps1 -Variant Release
```

Use a dedicated release/upload key for publishing. Android's debug key is for
testing and sideloading. See [APK builds and signing](docs/APK_SIGNING.md) for
Android Studio instructions and signature verification.

## Tests

```powershell
.\gradlew.bat :app:testDebugUnitTest :lint:test
```

## Repository layout

- `app/`: Android application and unit/UI tests.
- `lint/`: Project-specific Android lint checks and tests.
- `benchmark/`: Android performance benchmark sources.
- `docs/`: Product, architecture, and build documentation.
- `scripts/`: Build helpers.

Build outputs, APKs, local tooling, generated graph analysis, design-reference
exports, and review screenshots are excluded from Git. Signing credentials,
private keys, environment files, and local Firebase configuration stay local.
