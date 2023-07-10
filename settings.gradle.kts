rootProject.name = "full-stack-spring-example"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// Specify toolchains: https://github.com/gradle/foojay-toolchains
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.6.0"
}

include(
    "server",
    "server-api",
    "common-test",
    "app"
)
