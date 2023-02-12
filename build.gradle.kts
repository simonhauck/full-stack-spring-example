import net.researchgate.release.ReleaseExtension

plugins { id("net.researchgate.release") version "3.0.2" }

allprojects {
    group = "com.github.simonhauck.full-stack-spring-example"
    version = readVersionFromFile()
}

fun readVersionFromFile(): String {
    val versionPropertiesFile = "${rootDir.absolutePath}/version.properties"
    val fis = java.io.FileInputStream(versionPropertiesFile)
    val prop = java.util.Properties()
    prop.load(fis)

    return prop.getProperty("version")
}

configure<ReleaseExtension> {
    val versionPropertiesFile = "${rootDir.absolutePath}/version.properties"
    versionPropertyFile.set(versionPropertiesFile)

    tagTemplate.set("v\${version}")
    pushReleaseVersionBranch.set("master")
    with(git) { requireBranch.set("master") }
}
