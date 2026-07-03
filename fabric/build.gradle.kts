import java.nio.file.Paths

plugins {
    `anvilbuild-loader`
    id("fabric-loom") version "1.14-SNAPSHOT"
}

group = commonMod.prop("modGroup")
version = commonMod.prop("modVersion")

stonecutter {
    dependencies["fabric"] = commonMod.prop("fabricLoaderVersion")
    dependencies["fabric_api"] = commonMod.prop("fabricAPIVersion")

    constants["malilib"] = commonMod.prop("malilib") != "NONE"
    constants["malilibPreRewrite"] = commonMod.prop("malilibPreRewrite") == "TRUE"
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