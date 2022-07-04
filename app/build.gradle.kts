plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

val apkName = "TTS Translate Sample"

android {
    compileSdkVersion(31)

    defaultConfig {
        applicationId = "com.ttstranslate.app"
        setProperty("archivesBaseName", "ttstranslate")
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "0.0.5"
    }
    
    signingConfigs {
        create("ttstranslate") {
            storeFile = file("../ttstranslate.jks")
            keyAlias = "ttstranslate"
            if (project.hasProperty("PROJECT_KEY_PASSWORD") && project.hasProperty("PROJECT_KEYSTORE_PASSWORD")) {
                keyPassword = project.property("PROJECT_KEY_PASSWORD").toString()
                storePassword = project.property("PROJECT_KEYSTORE_PASSWORD").toString()
            } else {
                throw GradleException("Not found signing config password properties")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            addManifestPlaceholders(
                mapOf(
                    "enableCrashReporting" to false,
                    "usesCleartextTraffic" to true
                ))
        }
        create("staging") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("ttstranslate")
            addManifestPlaceholders(
                mapOf(
                    "enableCrashReporting" to true,
                    "usesCleartextTraffic" to true
                )
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("ttstranslate")
            addManifestPlaceholders(
                mapOf(
                    "enableCrashReporting" to true,
                    "usesCleartextTraffic" to false
                )
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        tasks.lint.get().enabled = true
        isAbortOnError = true
        isCheckAllWarnings = true
        isIgnoreWarnings = false
        isWarningsAsErrors = false
        isCheckDependencies = true
        htmlReport = true
        isExplainIssues = true
        isNoLines = false
        textOutput("stdout")
        disable("InvalidFragmentVersionForActivityResult")
    }
    
    afterEvaluate {
        applicationVariants.all {
            val variantName = this.name.capitalize()
            if (variantName != "Debug") {
                project.tasks["compile${variantName}Sources"].dependsOn(project.tasks["lint${variantName}"])
            }
        }
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs =
            freeCompilerArgs + "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi" + "-Xopt-in=kotlin.RequiresOptIn" + "-Xuse-experimental=kotlinx.coroutines.FlowPreview" + "-Xopt-in=io.insert-koin.component.KoinApiExtension" + "-Xopt-in=org.koin.core.component.KoinApiExtension" + "-X"
    }

    buildFeatures {
        viewBinding = true
    }

    bundle {
        language { enableSplit = false }
    }

    sourceSets {
        sourceSets.getByName("main") {
            java.srcDir("../resources/src/main/res")
            java.srcDir("src/main/res")
        }
    }
    
    packagingOptions.exclude("META-INF/*.kotlin_module")
}

dependencies {
    implementation (project(":data"))
    implementation (project(":domain"))
    implementation( project(":resources"))

    // Reflection
    implementation ("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra.get("kotlinVersion")}")

    // Desugaring
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:${rootProject.extra.get("desugaringVersion")}")

    // AndroidX
    implementation ("androidx.appcompat:appcompat:1.4.2")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation( "androidx.preference:preference-ktx:1.2.0")
    implementation ("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation ("androidx.annotation:annotation:1.4.0")
    implementation( "androidx.fragment:fragment-ktx:1.5.0")
    // MVVM
    val lifecycleVersion = "2.5.0"
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-process:$lifecycleVersion")

    // Single LiveData event
    implementation( "com.github.hadilq.liveevent:liveevent:1.2.0")

    // Coroutines
    val coroutinesVersion = "1.6.2"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Material
    implementation ("com.google.android.material:material:1.6.1")

    // Koin DI
    implementation ("io.insert-koin:koin-android:${rootProject.extra.get("koinVersion")}")

    // Cicerone navigation
    implementation( "com.github.terrakok:cicerone:7.0")

    // FastAdapter
    val fastAdapterVersion = "5.6.0"
    implementation( "com.mikepenz:fastadapter:$fastAdapterVersion")
    implementation( "com.mikepenz:fastadapter-extensions-diff:$fastAdapterVersion")
    implementation ("com.mikepenz:fastadapter-extensions-drag:$fastAdapterVersion")
    implementation ("com.mikepenz:fastadapter-extensions-scroll:$fastAdapterVersion")
    implementation ("com.mikepenz:fastadapter-extensions-swipe:$fastAdapterVersion")
    implementation ("com.mikepenz:fastadapter-extensions-ui:$fastAdapterVersion")
    implementation ("com.mikepenz:fastadapter-extensions-utils:$fastAdapterVersion")
    implementation ("com.mikepenz:fastadapter-extensions-binding:$fastAdapterVersion")

    // Image loader
    implementation ("io.coil-kt:coil:2.1.0")

    // Language
    implementation ("com.github.YarikSOffice:lingver:1.3.0")

    //KPermissions
    implementation ("com.github.fondesa:kpermissions:3.3.0")
    implementation ("com.github.fondesa:kpermissions-coroutines:3.3.0")

    //Lottie
    implementation("com.airbnb.android:lottie:4.1.0")

    //SwipeLayout
    implementation ("com.github.zerobranch:SwipeLayout:1.3.1")
}
