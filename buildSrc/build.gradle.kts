plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "KikuGie Releases"
        url = uri("https://maven.kikugie.dev/releases")
    }
    maven {
        name = "KikuGie Snapshots"
        url = uri("https://maven.kikugie.dev/snapshots")
    }
}

dependencies {
    implementation("dev.kikugie:stonecutter:0.9.6")
}