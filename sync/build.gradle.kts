plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ai.arena.aura.sync"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
}

kotlin { jvmToolchain(17) }

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(project(":core-common"))
    implementation(project(":core-domain"))
    implementation(project(":core-database"))
    implementation(project(":core-media"))
    implementation(libs.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
