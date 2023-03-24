plugins { id("com.google.cloud.tools.jib") }

jib {
    to {
        val registryPrefix = System.getenv("DOCKER_REGISTRY")?.plus("/") ?: ""
        val branchName = System.getenv("BRANCH_NAME") ?: "unknown"

        val projectVersion = project.version.toString()
        val releaseTag = if (!projectVersion.contains("SNAPSHOT")) "release" else null

        image = "$registryPrefix${project.rootProject.name}-${project.name}"
        tags = setOfNotNull("$projectVersion", branchName, releaseTag)
        auth {
            username = System.getenv("DOCKER_USR")
            password = System.getenv("DOCKER_PSW")
        }
    }
}
