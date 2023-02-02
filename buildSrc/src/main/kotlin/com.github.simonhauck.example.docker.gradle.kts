plugins {
    id("com.google.cloud.tools.jib")
}

jib {
    to {
        val registry = System.getenv("DOCKER_REGISTRY")?.plus("/") ?: ""
        val branchName = System.getenv("BRANCH_NAME") ?: "unknown"
        image = "$registry${project.rootProject.name}/${project.name}"
        tags = setOf("${project.version}", branchName)
        auth {
            username = System.getenv("DOCKER_USR")
            password = System.getenv("DOCKER_PSW")
        }
    }
}
