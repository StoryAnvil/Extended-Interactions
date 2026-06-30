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

dependencies {
    compileOnly("org.spongepowered:mixin:${commonMod.prop("mixinVersion")}")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
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