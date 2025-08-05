import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.guodong.android.system.permission.app"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.guodong.android.system.permission.app"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = project.file("rockchips.jks")
            storePassword = "123456"
            keyAlias = "code"
            keyPassword = "123456"
        }

        create("release") {
            storeFile = project.file("rockchips.jks")
            storePassword = "123456"
            keyAlias = "code"
            keyPassword = "123456"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    flavorDimensions += listOf("vendor")
    productFlavors {
        create("aosp") {
            applicationIdSuffix = ".aosp"
            versionNameSuffix = "-aosp"
            dimension = "vendor"
            resValue("string", "app_name", "AndroidSystemPermissionApp-aosp")
        }

        create("rockchips") {
            applicationIdSuffix = ".rockchips"
            versionNameSuffix = "-rockchips"
            dimension = "vendor"
            resValue("string", "app_name", "AndroidSystemPermissionApp-rockchips")
        }

        create("hikvision") {
            applicationIdSuffix = ".hikvision"
            versionNameSuffix = "-hikvision"
            dimension = "vendor"
            resValue("string", "app_name", "AndroidSystemPermissionApp-hikvision")
        }
    }

    sourceSets {
        getByName("aosp") {
            java.srcDirs("src/aosp/java")
        }

        getByName("rockchips") {
            java.srcDirs("src/rockchips/java")
        }

        getByName("hikvision") {
            java.srcDirs("src/hikvision/java")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JvmTarget.JVM_11.target
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)

    add("aospImplementation", project(":permission"))
    add("rockchipsImplementation", project(":permission-adapters:rockchips"))
    add("hikvisionImplementation", project(":permission-adapters:hikvision"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}