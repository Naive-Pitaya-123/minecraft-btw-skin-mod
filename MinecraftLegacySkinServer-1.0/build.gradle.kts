import org.gradle.jvm.tasks.Jar

group = "simonmeskens.legacyskinserver"
version = "1.0-SNAPSHOT"

apply {
    plugin("java")
}

repositories {
    mavenCentral()
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "Main-Class" to "simonmeskens.legacyskinserver.Server"
        ))
    }
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}