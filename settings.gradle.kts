rootProject.name = "full-stack-spring-example"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    "server",
    "server-api",
    "common-test",
    "app"
)
