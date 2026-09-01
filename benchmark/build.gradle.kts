plugins {
    alias(libs.plugins.android.test)
}

android {
    namespace = "com.example.algolens.benchmark"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // The benchmark module builds against the app's release variant (R8-minified).
    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    buildTypes {
        create("benchmark") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
        }
    }
}

dependencies {
    implementation(libs.benchmarkMacroJunit4)
    implementation(libs.androidxTestUiautomator)
    // AndroidJUnit4 runner + ext.junit for @RunWith. Required to compile the
    // macrobenchmark test classes (unresolved without this on AGP 9).
    implementation(libs.androidx.junit)
}
