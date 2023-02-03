allprojects {
    group = "com.github.simonhauck.full-stack-spring-example"
    version = readVersionFromFile()
}

fun readVersionFromFile(): String {
    val fis = java.io.FileInputStream("version.properties")
    val prop = java.util.Properties()
    prop.load(fis)

    return prop.getProperty("version")
}
