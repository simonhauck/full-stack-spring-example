plugins { id("com.google.cloud.tools.jib") }

jib {
    to {
        val registry2 = System.getenv("DOCKER_REGISTRY")?.plus("/") ?: "no registry found"
        val registryString = registry2.split("").joinToString("-")
        println(registryString)

        val registry = "shauck/"
        val branchName = System.getenv("BRANCH_NAME") ?: "unknown"
        val imageName = "$registry${project.rootProject.name}-${project.name}"

        println("registryEnv: $registryString")
        println("Image Name: $imageName")
        image = imageName
        tags = setOf("${project.version}", branchName)
        auth {
            val getenv = System.getenv("DOCKER_USR") ?: "No username found"
            println("Username: ${getenv.split("").joinToString("-")}")
            username = System.getenv("DOCKER_USR")
            password = System.getenv("DOCKER_PSW")
        }
    }
}
