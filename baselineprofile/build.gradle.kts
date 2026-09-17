plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.ashes.dev.works.system.core.internals.antar.baselineprofile"
    compileSdk = 37

    defaultConfig {
        // Baseline Profile generation needs Android 9+; unrooted devices need Android 13+.
        minSdk = 28
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    targetProjectPath = ":app"
}

baselineProfile {
    // Generate on a connected physical device (no Gradle-managed device on this machine).
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}
