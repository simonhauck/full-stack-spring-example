plugins {
	id("com.github.simonhauck.example.artifactory")
	id("com.github.simonhauck.example.kotlin-conventions")
	id("com.github.simonhauck.example.spring-conventions")
}

dependencies {
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

