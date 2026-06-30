plugins {
    id("java")
    id("idea")
    id("java-library")
}

version = "$loader-${commonMod.version}+${commonMod.minecraftVersion}"

base {
    archivesName = commonMod.id
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(commonProject.prop("javaVersion")!!)
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven {
                name = "Spongepowered"
                url = uri("https://repo.spongepowered.org/repository/maven-public")
            }
        }
        filter {
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
    maven {
        name = "Modrinth Maven"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        name = "Cursemaven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    maven {
        name = "KikuGie Releases"
        url = uri("https://maven.kikugie.dev/releases")
    }
    maven {
        name = "KikuGie Snapshots"
        url = uri("https://maven.kikugie.dev/snapshots")
    }
}

tasks {
    processResources {
        var forgeVersionChecker = ""
        if (commonMod.propOrNull("modrinthId") != null) {
            forgeVersionChecker = "updateJSONURL = \"https://api.modrinth.com/updates/${commonMod.propOrNull("modrinthId")}/forge_updates.json\""
        }
        val props = mapOf(
            "javaVersion" to commonMod.propOrNull("javaVersion"),
            "modId" to commonMod.id,
            "modName" to commonMod.propOrNull("modName"),
            "modVersion" to commonMod.propOrNull("modVersion"),
            "modFullVersion" to "${commonMod.minecraftVersion}-${commonMod.version}",
            "modGroup" to commonMod.propOrNull("modGroup"),
            "modAuthor" to commonMod.propOrNull("modAuthor"),
            "modLicense" to commonMod.propOrNull("modLicense"),
            "modDescription" to commonMod.propOrNull("modDescription"),
            "modURL" to commonMod.propOrNull("modURL"),
            "minecraftVersion" to commonMod.minecraftVersion,
            "minecraftVersionRange" to commonMod.propOrNull("minecraftVersionRange"),
            "neoForgeVersion" to commonMod.propOrNull("neoForgeVersion"),
            "neoForgeVersionRange" to commonMod.propOrNull("neoForgeVersionRange"),
            "fabricLoaderVersion" to commonMod.propOrNull("fabricLoaderVersion"),
            "fabricLoaderVersionRange" to commonMod.propOrNull("fabricLoaderVersionRange"),
            "fabricAPIVersion" to commonMod.propOrNull("fabricAPIVersion"),
            "fabricAPIVersionRange" to commonMod.propOrNull("fabricAPIVersionRange"),
        ).filterValues { it?.isNotEmpty() == true }.mapValues { (_, v) -> v!! }.toMutableMap()
        props["__NEOFORGE_UPDATE_CHECKER__"] = forgeVersionChecker
        val jsonProps = props.mapValues { (_, v) -> v.replace("\n", "\\\\n") }
        filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
            expand(props)
        }
        filesMatching(listOf("pack.mcmeta", "fabric.mod.json",
            "${commonMod.id}.mixins.json", "${commonMod.id}.neoforge.mixins.json", "${commonMod.id}.fabric.mixins.json")) {
            expand(jsonProps)
        }
        inputs.properties(props)
    }
}

tasks.named("processResources") {
    dependsOn(":common:${commonMod.minecraftVersion}:stonecutterGenerate")
}