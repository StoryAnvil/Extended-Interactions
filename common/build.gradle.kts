plugins {
    id("anvilbuild-common")
    id("net.neoforged.moddev") version "2.0.141"
}

neoForge {
    // This is common build script and no neoforge-specific features should be used.
    // NeoForge's ModDevGradle is used here in vanilla-mode (https://docs.neoforged.net/toolchain/docs/plugins/mdg/#vanilla-mode)
    neoFormVersion = "${commonMod.minecraftVersion}-${commonMod.prop("neoFormTimestamp")}"

    parchment {
        minecraftVersion = commonMod.minecraftVersion
        mappingsVersion = commonMod.prop("parchmentVersion")
    }
}

stonecutter {
    constants["patchouli"] = commonMod.prop("commonPatchouli") != "NONE"
    constants["oracle_index"] = commonMod.prop("commonOracleIndex") != "NONE"
}

dependencies {
    compileOnly("org.spongepowered:mixin:${commonMod.prop("mixinVersion")}")
    implementation("dev.isxander:yet-another-config-lib:${commonMod.prop("yaclVersion")}+${commonMod.minecraftVersion}-neoforge")
    if (commonMod.prop("commonPatchouli") != "NONE") {
        compileOnly("maven.modrinth:nU0bVIaL:${commonMod.prop("commonPatchouli")}")
    }
    if (commonMod.prop("commonOracleIndex") != "NONE") {
        compileOnly("maven.modrinth:J8MMsNrL:${commonMod.prop("commonOracleIndex")}")
    }
}

val commonJava: Configuration = configurations.create("commonJava") {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration = configurations.create("commonResources") {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    afterEvaluate {
        val mainSourceSet = sourceSets.main.get()
        mainSourceSet.java.sourceDirectories.files.forEach {
            add(commonJava.name, it)
        }
        mainSourceSet.resources.sourceDirectories.files.forEach {
            add(commonResources.name, it)
        }
    }
}