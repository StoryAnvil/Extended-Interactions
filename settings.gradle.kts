pluginManagement {
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
        maven {
            name = "NeoForged Releases"
            url = uri("https://maven.neoforged.net/releases")
        }
        maven {
            name = "FabricMC"
            url = uri("https://maven.fabricmc.net/")
        }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.6"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

var fabricVersions = providers.gradleProperty("anvilbuild_fabric_versions").orNull?.split(",")?.map {it.trim()} ?: emptyList()
var neoforgeVersions = providers.gradleProperty("anvilbuild_neoforge_versions").orNull?.split(",")?.map {it.trim()} ?: emptyList()
var loaders = mapOf(
    "fabric" to fabricVersions,
    "neoforge" to neoforgeVersions
)
var commonVersions = loaders.values.flatten().distinct()

stonecutter {
    create(rootProject) {
        versions(*commonVersions.toTypedArray())
        branch("common") {
            versions(*commonVersions.toTypedArray())//.buildscript("common/build.gradle.kts")
        }
        loaders.forEach { branchName, versions ->
            if (versions.isNotEmpty()) {
                branch(branchName) {
                    versions(*versions.toTypedArray())//.buildscript("$branchName/build.gradle.kts")
                }
            }
        }
        vcsVersion = providers.gradleProperty("anvilbuild_vsc_version").orNull as String
    }
}

rootProject.name = "ExtendedInteractions"