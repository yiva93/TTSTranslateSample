plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
}

android {
    compileSdkVersion(31)

    defaultConfig {
        minSdk = 23
        targetSdk = 31
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        create("staging") {
            isMinifyEnabled = true
            consumerProguardFiles("proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = true
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs =
            freeCompilerArgs + "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi" + "-Xopt-in=kotlin.RequiresOptIn" + "-Xuse-experimental=kotlinx.coroutines.FlowPreview" + "-Xopt-in=io.insert-koin.component.KoinApiExtension" + "-Xopt-in=org.koin.core.component.KoinApiExtension" + "-X"
    }
}

dependencies {
    implementation(project(":resources"))

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${rootProject.extra.get("desugaringVersion")}")

    // Kotlin
    api("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra.get("kotlinVersion")}")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    // Logging
    api("io.github.microutils:kotlin-logging:2.0.10")
    api("com.github.tony19:logback-android:2.0.0")

    val fastAdapterVersion = "5.6.0"
    api ("com.mikepenz:fastadapter-extensions-paged:${fastAdapterVersion}")
}