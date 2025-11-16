@file:OptIn(ExperimentalKotlinGradlePluginApi::class)
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.jetbrains.kotlin.serialization)

    //add room, ksp plugins
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            //koin for android
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            //Add Ktor for android(jvm same as desktop): to be able to use httpEngine
            implementation(libs.ktor.client.okhttp)

        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)


            implementation(compose.material) //added
            implementation(compose.materialIconsExtended) // <- Icons.Default.Search / Close

            //koin for compose
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.core)

            //Add coil bundle dep: check toml
            implementation(libs.bundles.coil)
            //add ktor bundle dep: check toml
            implementation(libs.bundles.ktor)

            //add compose nav from jetbrains
            implementation(libs.jetbrains.compose.navigation)

            //add room common dep and sqlite
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

        }

        nativeMain.dependencies {
            //Add ktor http engine(not okHttp that's jvm only) we use darwin engine on IOS
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        //I don't know why it did work in other project with Desktop, but here, its creating issue:
        //he KSP plugin needs to know which source set it should process. Since the Room compiler generates Android-specific code,
        // you need to apply it specifically to an Android source set.
//        dependencies {
//            ksp(libs.androidx.room.compiler)
//        }

    }
}






android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    // Add the KSP dependency for each target that uses Room, applies Room Compiler to Android/IOS targets
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

// Pass arguments to KSP
ksp {
    // This is a workaround for a known KSP issue with native targets.
    // It helps KSP correctly configure its tasks for iOS.
    // The issue is tracked here: https://github.com/google/ksp/issues/1183
    arg("room.generateKotlin", "true")
}
