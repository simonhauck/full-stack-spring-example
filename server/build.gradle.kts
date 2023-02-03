plugins {
    id("com.github.simonhauck.example.artifactory")
    id("com.github.simonhauck.example.kotlin-conventions")
    id("com.github.simonhauck.example.spring-conventions")

    // Generate open api doc
    id("org.springdoc.openapi-gradle-plugin") version "1.6.0"
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-web")

    // OpenApi Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":common-test"))

    // Check if the backend api matches the api definition
    testImplementation(project(":server-api"))
}

// User another port to have it not clashing with running instances
openApi {
    val apiGeneratedPort = 59186
    apiDocsUrl.set("http://localhost:$apiGeneratedPort/v3/api-docs/openapi.json")
    outputDir.set(file("${project(":server-api").projectDir}/src/main/resources"))

    customBootRun {
        args.set(
            listOf(
                "--server.port=$apiGeneratedPort",
            )
        )
    }
}
