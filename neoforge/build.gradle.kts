import java.nio.file.Paths

plugins {
    `anvilbuild-loader`
    id("net.neoforged.moddev") version "2.0.141"
}

group = commonMod.prop("modGroup")
version = commonMod.prop("modVersion")

neoForge {
    enable {
        version = commonMod.prop("neoForgeVersion")
    }
}

stonecutter {
    dependencies["neoforge"] = commonMod.prop("neoForgeVersion")
    constants["mafglib"] = commonMod.prop("mafglib") != "NONE"
    constants["mekanism"] = commonMod.prop("mekanism") != "NONE"
}

dependencies {
    implementation("dev.isxander:yet-another-config-lib:${commonMod.prop("yaclVersion")}+${commonMod.minecraftVersion}-neoforge")
    if (commonMod.prop("mafglib") != "NONE") {
        implementation("maven.modrinth:SKI34J7B:${commonMod.prop("mafglib")}")
    }
    if (commonMod.prop("neoforgePatchouli") != "NONE") {
        implementation("maven.modrinth:nU0bVIaL:${commonMod.prop("neoforgePatchouli")}")
    }
    if (commonMod.prop("neoforgeOracleIndex") != "NONE") {
        implementation("maven.modrinth:J8MMsNrL:${commonMod.prop("neoforgeOracleIndex")}")
    }
    if (commonMod.prop("neoforgeArchAPI") != "NONE") {
        runtimeOnly("maven.modrinth:lhGA9TYQ:${commonMod.prop("neoforgeArchAPI")}")
    }
    if (commonMod.prop("mekanism") != "NONE") {
        implementation("maven.modrinth:Ce6I4WUE:${commonMod.prop("mekanism")}")
    }
}

neoForge {
    runs {
        register("client") {
            var dir = Paths.get(rootProject.projectDir.path, "runs", "nf-client").toFile()
            if (!dir.exists()) dir.mkdirs()

            client()
            ideName = "NF Client (${project.path})"
            gameDirectory.set(dir)
        }
        register("server") {
            var dir = Paths.get(rootProject.projectDir.path, "runs", "nf-server").toFile()
            if (!dir.exists()) dir.mkdirs()

            server()
            ideName = "NF Server (${project.path})"
            gameDirectory.set(dir)
        }
    }

    parchment {
        minecraftVersion = commonMod.minecraftVersion
        mappingsVersion = commonMod.prop("parchmentVersion")
    }

    mods {
        register(commonMod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}