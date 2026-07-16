import java.nio.file.Paths

plugins {
    `anvilbuild-loader`
    id("fabric-loom") version "1.17-SNAPSHOT"
}

group = commonMod.prop("modGroup")
version = commonMod.prop("modVersion")

stonecutter {
    dependencies["fabric"] = commonMod.prop("fabricLoaderVersion")
    dependencies["fabric_api"] = commonMod.prop("fabricAPIVersion")

    constants["malilib"] = commonMod.prop("malilib") != "NONE"
    constants["malilibPreRewrite"] = commonMod.prop("malilibPreRewrite") == "TRUE"
}

repositories {
    maven {
        url = uri("https://maven.modmuss50.me/")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.minecraftVersion}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${commonMod.minecraftVersion}:${commonMod.prop("parchmentVersion")}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${commonMod.prop("fabricLoaderVersion")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.prop("fabricAPIVersion")}+${commonMod.minecraftVersion}")
    modImplementation("dev.isxander:yet-another-config-lib:${commonMod.prop("yaclVersion")}+${commonMod.minecraftVersion}-fabric")
    modImplementation("maven.modrinth:mOgUt4GM:${commonMod.prop("modmenuVersionID")}")

    if (commonMod.prop("malilib") != "NONE") {
        modImplementation("maven.modrinth:GcWjdA9I:${commonMod.prop("malilib")}")
    }
    if (commonMod.prop("fabricPatchouli") != "NONE") {
        modCompileOnly("maven.modrinth:nU0bVIaL:${commonMod.prop("fabricPatchouli")}")
    }
    if (commonMod.prop("fabricOracleIndex") != "NONE") {
        modImplementation("maven.modrinth:J8MMsNrL:${commonMod.prop("fabricOracleIndex")}")

        var libs: List<String> = Paths.get(rootProject.projectDir.path, "test_assets", "dev_libs", "oracle" + commonMod.minecraftVersion)
            .toFile().listFiles().map { v -> v.toPath().toAbsolutePath().toString() }

        modImplementation(files(libs))
    }
    if (commonMod.prop("fabricFiber") != "NONE") {
        modImplementation("me.zeroeightsix:fiber:${commonMod.prop("fabricFiber")}")
    }
    if (commonMod.prop("fabricArchAPI") != "NONE") {
        modImplementation("maven.modrinth:lhGA9TYQ:${commonMod.prop("fabricArchAPI")}")
    }
}

loom {
    runs {
        // fabric does not support absolute paths
        getByName("client") {
            var dir = Paths.get(rootProject.projectDir.path, "runs", "fc-client").toFile()
            if (!dir.exists()) dir.mkdirs()

            client()
            configName = "FC Client"
            ideConfigGenerated(true)
            runDir = "../../../runs/fc-client"
        }
        getByName("server") {
            var dir = Paths.get(rootProject.projectDir.path, "runs", "fc-server").toFile()
            if (!dir.exists()) dir.mkdirs()

            server()
            configName = "FC Server"
            ideConfigGenerated(true)
            runDir = "../../../runs/fc-server"
        }
    }
}