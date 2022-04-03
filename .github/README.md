# Sorus
[![discord-banner](https://img.shields.io/discord/712349956567990372?label=discord&style=for-the-badge&color=7289da)](https://discord.gg/KwnEKXm3Ka)


A WIP minecraft client, which attempts to have features which are not super common in the rest of client development.

## Features
TODO

## Building
**1)** Download The Source

**2)** Open The Gradle Project With An IDE (IntelliJ has been tested the most so probably the best)

**2.5)** Wait for Kiln (gradle plugin) to do its thing (aka downloading dev env stuff) (this could take quite a while (10+ minutes) but will only need to be done once forever per version of mc)

**3)** Open a terminal in the project directory and type `./gradlew genRunConfiguration -Pconfiguration={ide},client,{version}`. `{ide}` would be `idea` if on IntelliJ, and `version` could be 1.8.9 or 1.18.2, depending on what version you want to test.

**4)** A run configuration should be generated in your ide. (if on IntelliJ and when you run the configuration you recieve and error complaining about `net.minecraft.client.main.Main` not existing, then switch the module of the run configuration to `client`, as opposed to `client.main`.

If you encounter any issues, join the discord for support.
