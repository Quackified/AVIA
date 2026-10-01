# AVIA APK builds and signing

The Gradle project and launcher name are AVIA. The application ID is `com.avia`.
The existing checkout folder remains `AlgoLens`; it does not determine the app's name.

## Android Studio setup

This machine has Android Studio, its bundled Java 21 JDK, Android SDK platform 37,
and SDK Build Tools 37 installed. The project uses the Gradle wrapper and requests
Java 21 in `gradle/gradle-daemon-jvm.properties`.

1. Open this project's `settings.gradle.kts` in Android Studio, or reopen the project
   to refresh its AVIA name.
2. Select **File > Sync Project with Gradle Files** after changing build settings.
3. In **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**,
   use Android Studio's bundled **jbr-21** JDK if a Gradle JDK selection is shown.
   Its local path is `C:/Program Files/Android/Android Studio/jbr`.
4. The SDK location in `local.properties` is
   `C:/Users/Quacky/AppData/Local/Android/Sdk`.
5. In **Build Variants**, choose `debug` for development or `release` for a
   non-debuggable APK. Use **Build > Build Bundle(s) / APK(s) > Build APK(s)**
   (called **Generate App Bundles or APKs > Generate APKs** in some Studio versions).

## Build from PowerShell

Run these commands from the project root:

```powershell
# Development APK, signed automatically with the local debug key
.\scripts\Build-Apk.ps1 -Variant Debug

# Non-debuggable APK, signed with the key in keystore.properties
.\scripts\Build-Apk.ps1 -Variant Release
```

The helper selects Android Studio's JDK for that command only. It avoids the
system Java 8 installation and leaves the system Java settings unchanged.
For a nonstandard Studio installation, pass `-JdkPath 'C:/your/Studio/jbr'`.

Outputs:

- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Signed release: `app/build/outputs/apk/release/app-release.apk`
- Without a signing configuration, Gradle produces `app-release-unsigned.apk`.

## Signing identity currently reused

The previously built `app-release.apk` was signed by this computer's **Android
Debug** key. The ignored local `keystore.properties` reuses exactly that key,
as requested, so subsequent builds keep the same signing identity.
This finding confirms the previous APK's signer; it does not confirm that the
app was ever published or distributed.

- Keystore: `C:/Users/Quacky/.android/debug.keystore`
- Alias: `androiddebugkey`
- Certificate SHA-256:
  `3AAE1CD309B0A679F64BCA713E5BF18CDC3512597628E498841E8668B44A6AF0`

These APKs are suitable for local testing and sideloading. Google Play does not
accept debug certificates for publishing, even if the build variant is `release`.
Keep this keystore if you need to update installations signed with it.

## Use a dedicated existing release key

1. Edit the ignored `keystore.properties` at the project root. The tracked
   `keystore.properties.example` shows its four required entries: `storeFile`,
   `storePassword`, `keyAlias`, and `keyPassword`.
2. Enter the details of your existing release/upload keystore locally. Use forward
   slashes in Windows paths. Relative keystore paths start at the project root.
   Java properties treat backslashes as escapes; write a literal backslash as `\\`.
3. Sync Gradle and build `release`. Gradle will sign automatically with that key.

The properties file and common private-key file extensions are excluded from Git.
Keep a separate secure backup of the keystore and its passwords. Reuse the correct
signing identity for future updates; changing it can prevent installation over an
existing app.

## Android Studio signing wizard

To choose a keystore directly in Studio, select **Build > Generate Signed
Bundle/APK > APK**, choose module `app`, then enter the keystore path, key alias,
and passwords. Choose `release` and finish the wizard.

For the current local key, use the path and alias above; Android's standard debug
store and key passwords are both `android`. For publishing, use your dedicated
release/upload key. If this is the first public release and no such key exists,
use **Create new** in the wizard, choose an alias such as `avia-upload`, and set
validity to at least 25 years. Save its details in `keystore.properties` afterward
if you want automatic signing. Do not put private passwords in chat or source code.

For Google Play, choose **Android App Bundle** in the wizard, or run
`:app:bundleRelease` with the production signing configuration. The bundle is
written to `app/build/outputs/bundle/release/app-release.aab`.
If you switch certificates, register the appropriate SHA-1/SHA-256 fingerprints
with Firebase/Google authentication before testing sign-in.

## Verify an APK's signer

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\37.0.0\apksigner.bat" `
    verify --verbose --print-certs `
    '.\app\build\outputs\apk\release\app-release.apk'
```

`Verifies` confirms signature validation; the certificate SHA-256 identifies
the signing key. You can also run `:app:signingReport` from Studio's Gradle window.

Official references: [Sign your app](https://developer.android.com/studio/publish/app-signing),
[Build from the command line](https://developer.android.com/build/building-cmdline),
and [Java versions in Android builds](https://developer.android.com/build/jdks).
