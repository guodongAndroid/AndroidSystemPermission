plugins {
    id("maven-publish")
    id("signing")
}

configurations.maybeCreate("default")
artifacts.add("default", file("HikSDK_V1.2.2.aar"))

// region modify by john.wick on 2025/8/19 10:56 对于新的 Maven Central Portal，目前还没有可用的独立上传工件
// 的插件，且 Gradle 官方的 maven-publish 插件也尚不支持，所以此仓库暂时使用 maven-publish 插件生成 Maven 仓库的
// 标准布局，然后再使用 Maven Central Portal 的 Web UI 进行上传
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.sunxiaodou.android"
            artifactId = "system-permission-aar-hikvision"
            version = "1.0.1"

            artifact(file("HikSDK_V1.2.2.aar"))

            pom {
                name.set("AndroidSysetmPermission")
                description.set("System API interface provided by Hikvision")
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
    }

    repositories {
        maven {
            name = "Local"
            url = project.uri("repo")
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}



