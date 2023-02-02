import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`
    id("jacoco")
    id("org.sonarqube")
    id("com.adarshr.test-logger")
    id("com.coditory.integration-test")
}

// Kotlin Config
kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(Constants.JAVA_VERSION))
        this.vendor.set(Constants.JAVA_VENDOR)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "${Constants.JAVA_VERSION}"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

// Test config
tasks.withType<Test>() {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

sonarqube {
    properties {
        property("sonar.projectName", "${project.rootProject.name} :: ${project.name}")
        property("sonar.projectKey", "com.bruker.${project.rootProject.name}.${project.name}")
    }
}



