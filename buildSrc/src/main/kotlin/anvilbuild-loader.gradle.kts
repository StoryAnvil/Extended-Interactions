plugins {
    id("java")
    id("idea")
    id("anvilbuild-common")
}

val commonJava: Configuration = configurations.create("commonJava") {
    isCanBeResolved = true
}
    val commonResources: Configuration = configurations.create("commonResources") {
    isCanBeResolved = true
}

dependencies {
    val commonPath = common.hierarchy.toString()
    compileOnly(project(path = commonPath))
    commonJava(project(path = commonPath, configuration = "commonJava"))
    commonResources(project(path = commonPath, configuration = "commonResources"))
}

tasks {
    compileJava {
        dependsOn(commonJava)
        source(commonJava)
    }

    processResources {
        dependsOn(commonResources)
        from(commonResources)
    }
}