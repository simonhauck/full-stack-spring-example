plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
    mavenCentral()
}

dependencies {
    // Docker container
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.0")

    // Sonar
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:4.4.0.3356")

    // Add integration tests
    implementation("com.coditory.gradle:integration-test-plugin:1.4.5")

    // Print out test results nicely
    implementation("com.adarshr:gradle-test-logger-plugin:4.0.0")

    // Kotlin version
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")

    // Kotlin formatting
    implementation("com.ncorti.ktfmt.gradle:plugin:0.12.0")

    // Spring plugins
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.1.3")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")
    implementation("org.jetbrains.kotlin:kotlin-noarg:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.9.21")
}
