import java.io.FileInputStream
import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "AndroidSystemPermissionApp"
include(":app")
include(":permission-api")
include(":permission-aars:hikvision")
include(":permission-adapters:hikvision")
include(":permission-adapters:dwin")
include(":permission-adapters:signway")
include(":permission-adapters:aosp")

var hiddenApiRoot = "permission-hidden-api"

val localPropFile = file("local.properties")
val localProps = Properties()

if (localPropFile.canRead()) {
    localProps.load(FileInputStream(localPropFile))
    if (localProps["hidden.api.useLocal"] == "true") {
        hiddenApiRoot = localProps["hidden.api.dir"] as String
    }
}

includeBuild(hiddenApiRoot)
