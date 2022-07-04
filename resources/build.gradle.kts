plugins {
    id("com.android.library")
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
}

dependencies {
    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${rootProject.extra.get("desugaringVersion")}")
}
