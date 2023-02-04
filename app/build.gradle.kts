import org.gradle.internal.os.OperatingSystem
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("com.github.simonhauck.example.artifactory")
    id("org.openapi.generator") version "6.3.0"
}

val openApiBinding: Configuration by configurations.creating {}

dependencies { openApiBinding(project(":server-api")) }

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

// TODO Simon.Hauck 2023-02-03 - check caching behavior
val copyApiBindingTask =
    tasks.register<Copy>("copyApiBinding") {
        dependsOn(openApiBinding)
        from(zipTree(openApiBinding.singleFile))
        into(openApiDirectory)

        doFirst { delete(openApiDirectory) }
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

val prepareEnvTask = tasks.register("prepareEnv") { dependsOn(generateFlutterBindingTask) }

val buildReleaseApkTask =
    tasks.register("buildReleaseApk") {
        val outputDir = "$buildDir/app/outputs/flutter-apk"
        dependsOn(prepareEnvTask)
        // This is not 100% perfect, because the android folder could also have an effect on the
        // output, but this is also always changing and breaking the build
        inputs.files(
            "$projectDir/lib",
            "$clientCode",
            "pubspec.yaml",
            "pubspec.lock",
        )
        outputs.dir(outputDir)
        doFirst { delete(outputDir) }
        doLast { runFlutterCommand("flutter build apk --release") }
    }

val buildWebReleaseTask =
    tasks.register("buildWebRelease") {
        val outputDir = "$buildDir/web"
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
        doLast { runFlutterCommand("flutter build web --release") }
    }

fun runFlutterCommand(flutterCommand: String, workDir: String = projectDir.absolutePath) {
    exec {
        val commandPrefix = getOsCommandPrefix()
        val command = commandPrefix.plus(listOf("\"$flutterCommand\""))
        println("Executing $command in $workDir")
        environment = System.getenv().toMap()
        workingDir = file(workDir)
        commandLine = command
    }
}

fun getOsCommandPrefix() =
    if (OperatingSystem.current().isWindows) listOf("cmd.exe", "/c") else emptyList()
