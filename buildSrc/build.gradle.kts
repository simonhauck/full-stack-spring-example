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
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.3")

    // Sonar
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:5.1.0.4882")

    // Add integration tests
    implementation("com.coditory.gradle:integration-test-plugin:1.5.1")

    // Print out test results nicely
    implementation("com.adarshr:gradle-test-logger-plugin:4.0.0")

    // Kotlin version
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")

    // Kotlin formatting
    implementation("com.ncorti.ktfmt.gradle:plugin:0.20.1")

    // Spring plugins
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.4")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.6")
    implementation("org.jetbrains.kotlin:kotlin-noarg:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.0.21")
}
