plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    ios()
    iosSimulatorArm64()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }
    js(IR) {
        moduleName = project.name
        browser {
            commonWebpackConfig {
                outputFileName = "$moduleName.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ballast.core)
                implementation(libs.ballast.navigation)
            }
        }
    }
}

android {
    namespace = "com.adrianwitaszak.ballastsharedui.${project.name}"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
