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
        debug {
            signingConfig = signingConfigs["debug"]
        }

        release {
            signingConfig = signingConfigs["release"]
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

    flavorDimensions += "vendor"
    val vendors = listOf("aosp", "hikvision", "dwin", "signway")
    productFlavors {
        vendors.forEach { vendor ->
            create(vendor) {
                applicationIdSuffix = ".$vendor"
                versionNameSuffix = "-$vendor"
                dimension = "vendor"
                resValue("string", "app_name", "AndroidSystemPermissionApp-$vendor")
            }
        }
    }

    sourceSets {
        vendors.forEach { vendor ->
            getByName(vendor) {
                java.srcDirs("src/$vendor/java")
            }
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

androidComponents {
    onVariants { variant ->
        val name = variant.name
        when (name) {
            "aospDebug" -> dependencies.add("aospDebugImplementation", dependencies.project(":permission-adapters:aosp"))
            "aospRelease" -> dependencies.add("aospReleaseImplementation", libs.permission.adapter.aosp)
            "hikvisionDebug" -> dependencies.add("hikvisionDebugImplementation", dependencies.project(":permission-adapters:hikvision"))
            "hikvisionRelease" -> dependencies.add("hikvisionReleaseImplementation", libs.permission.adapter.hikvision)
            "dwinDebug" -> dependencies.add("dwinDebugImplementation", dependencies.project(":permission-adapters:dwin"))
            "dwinRelease" -> dependencies.add("dwinReleaseImplementation", libs.permission.adapter.dwin)
            "signwayDebug" -> dependencies.add("signwayDebugImplementation", dependencies.project(":permission-adapters:signway"))
            "signwayRelease" -> dependencies.add("signwayReleaseImplementation", libs.permission.adapter.signway)
        }
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
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.viewpager2)

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)

    implementation(libs.file.picker)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}