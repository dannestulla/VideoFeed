import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_11) } }
    iosX64(); iosArm64(); iosSimulatorArm64(); jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.upload.domain)
            implementation(libs.ktor.client.core)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "br.gohan.videofeed.feature.upload.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
}
