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
}

dependencies {}

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