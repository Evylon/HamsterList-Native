import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    kotlin("android")
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

val buildTag = System.getenv("GITHUB_RUN_NUMBER") ?: DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())!!

android {
    namespace = "org.stratum0.hamsterlist.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "org.stratum0.hamsterlist.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.1+$buildTag"
    }
    signingConfigs {
        register("app") {
            keyAlias = "hamsterList_release"
            keyPassword = System.getenv("SIGNING_PASSWORD") ?: ""
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("SIGNING_PASSWORD") ?: ""
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs["app"]
        }
        named("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

detekt {
    buildUponDefaultConfig = true
    config = files("detekt.yml")
}

dependencies {
    implementation(project(":HamsterListCore"))

    // Compose Bill of Materials
    val composeBom = platform("androidx.compose:compose-bom:2024.04.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
}
