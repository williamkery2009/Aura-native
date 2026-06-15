plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ai.arena.aura"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "ai.arena.aura"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures { compose = true; buildConfig = true }
}

kotlin { jvmToolchain(17) }

dependencies {
    implementation(project(":core"))
    implementation(project(":core-ui"))
    implementation(project(":core-design"))
    implementation(project(":feature-home"))
    implementation(project(":feature-library"))
    implementation(project(":feature-search"))
    implementation(project(":feature-player"))
    implementation(project(":feature-playlists"))
    implementation(project(":feature-downloads"))
    implementation(project(":feature-settings"))
    implementation(project(":feature-lyrics"))
    implementation(project(":sync"))
    implementation(project(":analytics"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.timber)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
