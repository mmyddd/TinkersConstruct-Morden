# [Tinkers' Construct](https://github.com/SlimeKnights/TinkersConstruct) Unoffical Fork
**"This fork will make Tinkers' Construct 3 compatible with Mod GTM in all aspects."**  

# [Tinkers' Construct](https://slimeknights.github.io/projects/#tinkers-construct)

Modify all the things, then do it again!   
Melt down any metals you find. 	 
Power the world with spinning wind!

## Documentation

For documentation on writing addons or working with Tinkers' Consrtuct datapacks, see the pages on the SlimeKnight's Github.io pages: https://slimeknights.github.io/docs/

## Setting up a Workspace/Compiling from Source

Development Setup
* First, ensure you have IntelliJ IDEA and Java 17 installed. It is highly recommended to configure IDEA to use its bundled JetBrains Runtime (JBR) for optimal performance and stability; you can set this in Settings > Build Tools > Gradle > Gradle JVM. Clone the repository and open it as a Gradle project in IDEA, allowing the initial setup and dependency sync to complete. Once synced, run the genIntellijRuns Gradle task to automatically generate the necessary run configurations for the IDE.

* The project is pre-configured with three primary run configurations. The client run launches a Minecraft instance for testing gameplay and UI, while the server run starts a dedicated server for backend logic. Crucially, the data run executes the data generator, which scans your code to create asset files like recipes and models, outputting them to src/generated/resources/. This automated generation is a core part of the modern modding workflow.

* To build the project, run the gradlew build command. This will compile your code and package the mod into the build/libs/ directory. If you encounter any obscure Gradle issues during development, try running gradlew clean followed by gradlew cleanCache to reset your environment. Note that the build script is configured to skip tests to avoid known issues with the current Forge plugin.

## Issue reporting
Please include the following:

* Minecraft version
* Tinkers' Construct version
* Forge version/build
* Versions of any mods potentially related to the issue 
* Any relevant screenshots are greatly appreciated.
* For crashes:
	* Steps to reproduce
	* latest.log (the FML log) from the root folder of the client

## Licenses
Code, Textures and binaries are licensed under the [MIT License](https://tldrlegal.com/license/mit-license).

You are allowed to use the mod in your modpack.
Any modpack which uses Tinkers' Construct takes **full** responsibility for user support queries. For anyone else, we only support official builds from the main CI server, not custom built jars. We also do not take bug reports for outdated builds of Minecraft.

If you have queries about any license or the above support restrictions, please drop by our IRC channel, #TinkersConstruct on irc.esper.net

Any alternate licenses are noted where appropriate.=
