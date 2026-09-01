plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.android.lint)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.lint.api)
    compileOnly(libs.lint.checks)

    testImplementation(libs.lint)
    testImplementation(libs.lint.tests)
    testImplementation(libs.junit)
}
