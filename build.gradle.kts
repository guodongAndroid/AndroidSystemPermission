import com.android.build.api.variant.BuildConfigField
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.refin) apply false
    alias(libs.plugins.maven.publish) apply false
}

subprojects {
    plugins.withId("com.android.library") {
        apply(plugin = "com.vanniktech.maven.publish")

        val version = "1.0.2-SNAPSHOT"

        extensions.configure<LibraryAndroidComponentsExtension> {
            onVariants { variant ->
                variant.buildConfigFields?.put(
                    "SDK_VERSION",
                    BuildConfigField("String", "\"$version\"", "SDK Version Name")
                )
            }
        }

        extensions.configure<MavenPublishBaseExtension> {
            configure(
                AndroidSingleVariantLibrary(
                    "release",
                    sourcesJar = true,
                    publishJavadocJar = true
                )
            )

            publishToMavenCentral(true)
            signAllPublications()

            val groupId = "com.sunxiaodou.android"

            val parent = parent
            val artifactIdSuffix = if (name.endsWith("api")) {
                "api"
            } else if (parent != null && parent.name.endsWith("adapters")) {
                "adapter-$name"
            } else {
                throw IllegalArgumentException("Unknown project($name), and parent(${parent?.name})")
            }
            val artifactId = "system-permission-$artifactIdSuffix"

            println("Maven Coordinates: $groupId:$artifactId:$version")
            coordinates(groupId, artifactId, version)

            pom {
                name.set("AndroidSysetmPermission")
                description.set("A library that facilitates calling the Android system-level permissions interface")
                inceptionYear.set("2025")
                url.set("https://github.com/guodongAndroid/AndroidSystemPermission")

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("guodongAndroid")
                        name.set("guodongAndroid")
                        url.set("https://github.com/guodongAndroid/")
                    }
                }

                scm {
                    url.set("https://github.com/guodongAndroid/AndroidSystemPermission")
                    connection.set("scm:git:git://github.com/guodongAndroid/AndroidSystemPermission.git")
                    developerConnection.set("scm:git:ssh://git@github.com/guodongAndroid/AndroidSystemPermission.git")
                }
            }
        }

        extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "Local"
                    url = rootProject.uri("repo")
                }
            }
        }
    }
}