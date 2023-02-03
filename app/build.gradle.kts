import org.gradle.internal.os.OperatingSystem
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("com.github.simonhauck.example.artifactory")
    id("org.openapi.generator") version "6.2.1"
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
            exec {
                val commandPrefix = getOsCommandPrefix()
                val command = commandPrefix.plus(listOf("\"flutter pub get\""))
                println(System.getenv())
                environment = System.getenv().toMap()
                workingDir = file(clientCode)
                commandLine = command
            }
            exec {
                val commandPrefix = getOsCommandPrefix()
                val command = commandPrefix.plus("\"flutter pub run build_runner build\"")
                environment = System.getenv().toMap()
                workingDir = file(clientCode)
                commandLine = command
            }
        }
    }

val prepareEnvTask = tasks.register("prepareEnv") { dependsOn(generateFlutterBindingTask) }

fun getOsCommandPrefix() =
    if (OperatingSystem.current().isWindows) listOf("cmd.exe", "/c") else emptyList()
