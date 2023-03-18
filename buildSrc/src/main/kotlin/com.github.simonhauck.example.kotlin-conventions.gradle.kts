import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`
    id("jacoco")
    id("org.sonarqube")
    id("com.adarshr.test-logger")
    id("com.coditory.integration-test")
    id("com.ncorti.ktfmt.gradle")
    idea
}

// ---------------------------------------------------------------------------------------------------------------------
// Kotlin config
// ---------------------------------------------------------------------------------------------------------------------

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

// ---------------------------------------------------------------------------------------------------------------------
// IntegrationTest config
// ---------------------------------------------------------------------------------------------------------------------

// So that tests are correctly shown in the create test dialog and the test naming conventions are
// applied
idea {
    module {
        testSources.from(testSources, sourceSets.integration.get().kotlin.srcDirs)
        testResources.from(testResources, sourceSets.integration.get().resources.srcDirs)
    }
}

// ---------------------------------------------------------------------------------------------------------------------
// Kotlin formatter
// ---------------------------------------------------------------------------------------------------------------------

ktfmt {
    // KotlinLang style - 4 space indentation - From kotlinlang.org/docs/coding-conventions.html
    kotlinLangStyle()

    blockIndent.set(4)
    continuationIndent.set(4)

    removeUnusedImports.set(true)
}

tasks.register("checkFormat") { dependsOn(tasks.ktfmtCheck) }

tasks.register("format") { dependsOn(tasks.ktfmtFormat) }

// ---------------------------------------------------------------------------------------------------------------------
// Testing config
// ---------------------------------------------------------------------------------------------------------------------

tasks.withType<Test>() {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }

sonarqube {
    properties {
        property("sonar.projectName", "${project.rootProject.name} :: ${project.name}")
        property("sonar.projectKey", "com.github.example.${project.rootProject.name}.${project.name}")
    }
}
