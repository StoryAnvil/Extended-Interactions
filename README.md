# AnvilBuild Template
This is multi-loader multi-version template for Fabric and Neoforge.
[Stonecutter](stonecutter.kikugie.dev) is utilized for developing mods for multiple minecraft version
in single codebase.

This allows updating your mod for all minecraft versions and all mod loaders by writing code just once
instead of reimplementing single feature for each version and mod loader.

This template is based on [Cosmic lab studio's multiloader stonecutter template](https://github.com/cosmiclabstudio/multiloader-stonecutter).

Template supports Gradle 9.3+, Gradle 8 is not supported. LexForge support for older minecraft version
is planned.

## Prerequisites
To work with AnvilBuild template you need [IntelliJ IDEA](https://www.jetbrains.com/idea/). Eclipse support is not yet
implemented. [Stonecutter IntelliJ IDEA plugin](https://plugins.jetbrains.com/plugin/25044-stonecutter-dev) is recommened, but not required.

You can read [stonecutter's wiki](https://stonecutter.kikugie.dev/wiki/) for better understanding of this template.

At least basic Gradle and minecraft modding understanding is also important. This is not
a beginner-friendly template.

## Setting up
1. Create GitHub repository based on this template. [Click here](https://github.com/new?template_name=anvilbuild-template&template_owner=StoryAnvil).
If you do not use GitHub you can clone the template and unlink local copy from git repostiory.
```bash
$ git clone https://github.com/StoryAnvil/anvilbuild-template.git . && rm -rf .git
```
2. Open `gradle.properties` in root project's directory.<br>
Replace values in `Mod properties` category and change targeted minecraft versions if needed.
3. Sync Gradle project. It might fail if you added more versions to target. This is normal
4. If you added new versions, open `./versions` (in project's root. **not** `./fabric/versions` or `./neoforge/versions`[!]) and create `gradle.properties`
in each folder named after minecraft version you target.<br>
Paste following to created properties files:
```properties
# Toolchain
minecraftVersion=<minecraft versions you target>
minecraftVersionRange=[<minecraft versions you target>,<next minecraft version>)
neoFormTimestamp=<neoform timestamp for targeted minecraft version>
neoForgeVersion=<targeted neoforge version (like one you choose in launchers when you create minecraft instance)>
neoForgeVersionRange=[<min neoforge version supported>,)
fabricLoaderVersion=<targeted fabric version>
fabricLoaderVersionRange=*
fabricAPIVersion=<targeted fabric api version>
fabricAPIVersionRange=*
javaVersion=<java version used by targeted minecraft version>
```
You can get neoform timestamp [here](https://projects.neoforged.net/neoforged/neoform) by choosing targeted minecraft version
in form labeled `Select version` and retyping timestamp value below.
5. Rename java packages and `.mixins.json` files for names you use
6. Sync Gradle project again. Now it should run successfully if not you can create [GitHub issue](https://github.com/StoryAnvil/anvilbuild-template/issues/new).
