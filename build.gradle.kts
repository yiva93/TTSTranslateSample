// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    val kotlinVersion by rootProject.extra { "1.6.21" }
    val koinVersion by rootProject.extra { "3.2.0" }
    val desugaringVersion by rootProject.extra { "1.1.5" }
    val firebaseBomVersion by rootProject.extra { "28.4.0" }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.google.com/") }
    }
}
// subprojects {
//     afterEvaluate {
//         android {
//             compileSdkVersion(31)
//
//             defaultConfig {
//                 minSdkVersion (23)
//                 targetSdkVersion (31)
//             }
//
//             buildTypes {
//                 debug {
//                     isMinifyEnabled = false
//                 }
//                 internal {
//                     isMinifyEnabled  = true
//                 }
//                 staging {
//                     isMinifyEnabled = true
//                 }
//                 release {
//                     isMinifyEnabled = true
//                 }
//             }
//             compileOptions {
//                 isCoreLibraryDesugaringEnabled = true
//                 sourceCompatibility = JavaVersion.VERSION_11
//                 targetCompatibility = JavaVersion.VERSION_11
//             }
//
//             kotlinOptions {
//                 jvmTarget = "11"
//                 freeCompilerArgs =
//                     freeCompilerArgs + "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi" + "-Xopt-in=kotlin.RequiresOptIn" + "-Xuse-experimental=kotlinx.coroutines.FlowPreview" + "-Xopt-in=io.insert-koin.component.KoinApiExtension" + "-Xopt-in=org.koin.core.component.KoinApiExtension" + "-X"
//             }
//         }
//     }
// }

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}