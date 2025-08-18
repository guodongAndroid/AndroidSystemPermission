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
