plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

var libreTranslateApi = "\"https://libretranslate.de\""
android {
    compileSdkVersion(31)
    defaultConfig {
        minSdk = 23
        targetSdk = 31
        buildConfigField("int", "SCHEMA_VERSION", "1")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            buildConfigField("String", "LIBRE_TRANSLATE_API_ENDPOINT", libreTranslateApi)
        }
        create("staging") {
            isMinifyEnabled = true
            consumerProguardFiles("proguard-rules.pro")
            buildConfigField("String", "LIBRE_TRANSLATE_API_ENDPOINT", libreTranslateApi)
        }
        getByName("release") {
            isMinifyEnabled = true
            consumerProguardFiles("proguard-rules.pro")
            buildConfigField("String", "LIBRE_TRANSLATE_API_ENDPOINT", libreTranslateApi)
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
    implementation(project(":domain"))
    implementation(project(":resources"))

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${rootProject.extra.get("desugaringVersion")}")

    // JSON serialization
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    // Retrofit 2
    val retrofitVersion = "2.9.0"
    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    api("com.squareup.retrofit2:converter-jackson:2.7.2")
    // OkHttp 3
    val okhttpVersion = "4.9.3"
    api("com.squareup.okhttp3:okhttp:$okhttpVersion")
    api("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    // Shared preferences
    api("com.github.tfcporciuncula.flow-preferences:flow-preferences:1.3.2")

    // Room DB
    val roomVersion = "2.4.2"
    api("androidx.room:room-runtime:${roomVersion}")
    implementation("androidx.room:room-ktx:${roomVersion}")
    kapt("androidx.room:room-compiler:${roomVersion}")
}