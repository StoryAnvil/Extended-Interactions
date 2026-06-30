plugins {
    `anvilbuild-loader`
    id("fabric-loom") version "1.14-SNAPSHOT"
}

group = commonMod.prop("modGroup")
version = commonMod.prop("modVersion")

stonecutter {
    dependencies["fabric"] = commonMod.prop("fabricLoaderVersion")
    dependencies["fabric_api"] = commonMod.prop("fabricAPIVersion")
}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.minecraftVersion}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${commonMod.minecraftVersion}:${commonMod.prop("parchmentVersion")}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${commonMod.prop("fabricLoaderVersion")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.prop("fabricAPIVersion")}+${commonMod.minecraftVersion}")
}

loom {
    runs {
        getByName("client") {
            client()
            configName = "FC Client"
            ideConfigGenerated(true)
        }
        getByName("server") {
            server()
            configName = "FC Server"
            ideConfigGenerated(true)
        }
    }
}