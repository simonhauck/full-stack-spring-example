import org.gradle.internal.os.OperatingSystem
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("com.github.simonhauck.example.artifactory")
    id("org.openapi.generator") version "6.3.0"
}

val apiSpecification: Configuration by configurations.creating {}

dependencies { apiSpecification(project(":server-api", "json")) }

// ---------------------------------------------------------------------------------------------------------------------
// Generate flutter binding
// ---------------------------------------------------------------------------------------------------------------------

val apiBindingBaseFolder = "$buildDir/api-binding"
val openApiDirectory = "$apiBindingBaseFolder/specification"
val clientCode = "$apiBindingBaseFolder/client"

openApiGenerate {
    generatorName.set("dart-dio")
    inputSpec.set("$openApiDirectory/openapi.json")
    outputDir.set(clientCode)

    configOptions.set(
        mapOf(
            "pubAuthor" to "Spring Fullstack Example App",
            "pubAuthorEmail" to "example@gmx.de",
            "pubDescription" to "Flutter api binding for the server application",
            "pubVersion" to "${project.version}",
            "pubName" to "server",
            "pubLibrary" to "server.api",
        )
    )
}

val copyApiBindingTask =
    tasks.register<Copy>("copyApiBinding") {
        performCachedCopy(openApiDirectory, apiSpecification.files)
    }

val generateFlutterBindingTask =
    tasks.withType<GenerateTask> {
        dependsOn(copyApiBindingTask)
        inputs.files(openApiDirectory)
        outputs.dir(clientCode)
        doFirst {
            println("Deleting api binding in $clientCode$")
            delete(clientCode)
        }

        doLast {
            runFlutterCommand("flutter pub get", clientCode)
            runFlutterCommand("flutter pub run build_runner build", clientCode)
        }
    }

val installPubDependenciesTask =
    tasks.withType<GenerateTask> {
        dependsOn(generateFlutterBindingTask)
        inputs.files("pubspec.yaml", clientCode)
        outputs.files("pubspec.lock")

        doLast { runFlutterCommand("flutter pub get") }
    }

val prepareEnvTask = tasks.register("prepareEnv") { dependsOn(installPubDependenciesTask) }

// ---------------------------------------------------------------------------------------------------------------------
// Build App
// ---------------------------------------------------------------------------------------------------------------------

val buildReleaseApkTask =
    tasks.register("buildReleaseApk") {
        val outputDir = "$buildDir/app/outputs/flutter-apk"
        dependsOn(prepareEnvTask)
        // This is not 100% perfect, because the android folder could also have an effect on the
        // output, but this is also always changing and breaking the build
        inputs.files(
            "$projectDir/lib",
            clientCode,
            "pubspec.yaml",
            "pubspec.lock",
        )
        outputs.dir(outputDir)
        doFirst { delete(outputDir) }
        doLast { runFlutterCommand("flutter build apk --release") }
    }

// ---------------------------------------------------------------------------------------------------------------------
// Build Web
// ---------------------------------------------------------------------------------------------------------------------

val flutterWebBuildDir = "$buildDir/web"

val buildWebReleaseTask =
    tasks.register("buildWebRelease") {
        val outputDir = flutterWebBuildDir
        dependsOn(prepareEnvTask)
        // This is not 100% perfect, because the android folder could also have an effect on the
        // output, but this is also always changing and breaking the build
        inputs.files(
            "$projectDir/lib",
            "${projectDir}/web",
            "$clientCode",
            "pubspec.yaml",
            "pubspec.lock",
        )
        outputs.dir(outputDir)
        doFirst { delete(outputDir) }
        doLast { runFlutterCommand("flutter build web --web-renderer canvaskit --release") }
    }

val zipWebReleaseTask =
    tasks.register<Zip>("zipWebRelease") {
        dependsOn(buildWebReleaseTask)
        from(flutterWebBuildDir)
        destinationDirectory.set(file("$buildDir/zip"))
        archiveBaseName.set("flutter-web")
    }

tasks.register("assemble") { dependsOn(buildReleaseApkTask, zipWebReleaseTask) }

val webDistZipConfig = configurations.create("default")

artifacts.add(webDistZipConfig.name, zipWebReleaseTask.get())

// ---------------------------------------------------------------------------------------------------------------------
// Test tasks
// ---------------------------------------------------------------------------------------------------------------------

val flutterTestTask =
    tasks.register("test") {
        dependsOn(prepareEnvTask)
        runFlutterCommand("flutter test")
    }

tasks.register("integrationTest") { println("No integration tests defined for app module") }

// ---------------------------------------------------------------------------------------------------------------------
// Utility
// ---------------------------------------------------------------------------------------------------------------------

tasks.register("clean") { runFlutterCommand("flutter clean") }

fun runFlutterCommand(flutterCommand: String, workDir: String = projectDir.absolutePath) {
    exec {
        val command = flutterCommand.toOsCommand()
        println("Executing $command in $workDir")
        environment = System.getenv().toMap()
        workingDir = file(workDir)
        commandLine = command
    }
}

fun String.toOsCommand(): List<String> {
    return if (OperatingSystem.current().isWindows) listOf("cmd.exe", "/c", this)
    else this.split(" ").toList()
}

// fun getOsCommandPrefix() =
//    if (OperatingSystem.current().isWindows) listOf("cmd.exe", "/c") else emptyList()

fun Copy.performCachedCopy(target: String, inputFiles: Set<Any>) {
    inputs.files(inputFiles)
    outputs.dir(target)
    doFirst { delete(target) }
    from(inputFiles)
    into(target)
}
