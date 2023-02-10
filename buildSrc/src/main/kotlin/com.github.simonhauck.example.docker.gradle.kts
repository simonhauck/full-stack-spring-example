plugins { id("com.google.cloud.tools.jib") }

jib {
    to {
        val registry2 = System.getenv("DOCKER_REGISTRY")?.plus("/") ?: ""
        println(registry2.split("").joinToString("-"))

        val registry = "shauck/"
        val branchName = System.getenv("BRANCH_NAME") ?: "unknown"
        image = "$registry${project.rootProject.name}-${project.name}"
        tags = setOf("${project.version}", branchName)
        auth {
            println(System.getenv("DOCKER_USR").split("").joinToString("-"))
            username = System.getenv("DOCKER_USR")
            password = System.getenv("DOCKER_PSW")
        }
    }
}
