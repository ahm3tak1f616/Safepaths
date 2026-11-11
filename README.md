# Safepaths

<p align="center">
  <img src="docs/assets/safepaths-banner-2.5.png" alt="A golden path winds across a grassy island toward subtle timber-framed settlement silhouettes." width="1000">
</p>

Let journeys shape the world: footsteps form paths, roads speed up travel, and unused trails return to nature. Customize your paths and pace, with optional MineColonies support for citizen-made paths that respect colony permissions.

[![CurseForge](https://img.shields.io/badge/CurseForge-Download-F16436?style=for-the-badge&logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/safepaths)
[![Modrinth](https://img.shields.io/badge/Modrinth-Download-1BD96A?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/mod/safepaths)
[![GitHub Releases](https://img.shields.io/badge/GitHub-Releases-24292F?style=for-the-badge&logo=github&logoColor=white)](https://github.com/ahm3tak1f616/Safepaths/releases)

[Install](#install) · [Settings](#settings) · [Compatibility](#compatibility) · [Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki)

<table>
<tr>
<td width="50%" valign="top">
<h3>👣 Trails shaped by your journeys</h3>
The routes you walk become part of the landscape. Grass, dirt, sand, and other supported ground turn into paths after repeated use — <strong>20 passes within one Minecraft day</strong> by default. Want to keep a patch untouched? Enable ground protection while sneaking.
</td>
<td width="50%" valign="top">
<h3>⚡ Roads that reward the journey</h3>
Enjoy a <strong>20% road speed bonus</strong> by default, or set a different bonus for each conversion. Grass and sand can lead to the same path block while keeping their own bonuses. Add permanent roads from your favorite building materials, too.
</td>
</tr>
<tr>
<td valign="top">
<h3>🌿 Let nature take the path back</h3>
Unused tracked paths return to their original terrain after <strong>three Minecraft days</strong> by default. Creation and decay have separate switches, so you choose how the landscape changes. Placed and shovel-made paths are not automatically given a decay timer.
</td>
<td valign="top">
<h3>🏘️ At home in your colony</h3>
MineColonies citizens can form paths as they travel. Colony permissions apply, and farmland, inventories, and MineColonies blocks are protected. <strong>MineColonies is optional</strong> — Safepaths also works on its own.
</td>
</tr>
</table>

## New in 2.5.0

- **Make each route your own:** speed bonuses per conversion, separate creation and decay controls, and optional protection while sneaking.
- **Choose blocks visually:** compact settings tabs, source-to-road previews, and a searchable inventory-style block picker.
- **Keep changes together:** the main **Done** saves settings and conversions; **Cancel** discards the whole draft.

## Install

Choose the jar that matches **both** your Minecraft version and loader:

| Minecraft | Loader | Java | File ending |
| --- | --- | --- | --- |
| **1.21.1** | NeoForge 21.1.235+ | 21 | `-1.21.1-neoforge.jar` |
| **1.20.1** | Forge 47.3.0+ | 17 | `-1.20.1-forge.jar` |

1. Install the matching loader and download Safepaths from a link above.
2. Put **one matching Safepaths jar** in your instance's `mods` folder.
3. Start the game and walk a short route back and forth to try it.

**Playing on a server?** Install Safepaths on the server. Clients do not need it to use the paths or speed bonus. Settings are controlled by the server owner.

**Updating to 2.5.0?** Back up your world and replace the previous Safepaths jar. Keep only one installed version. Existing configs and path records remain supported; conversion entries without `@ speed` keep using the global bonus.

## Settings

Open **Mods → Safepaths → Config**. Choose **Creation**, **Decay**, or **Speed**, then open **Block Conversions...** to edit your ground and road choices.

**Your roads, your pace.** Enter `12.5` or `12,5` for a +12.5% bonus. Leave a rule's speed empty to use the global bonus, or enter `0` for no bonus. Time values show their tick unit beside the field.

**Permanent roads:** choose the same block for source and road. Gravel already works this way by default. Missing-mod rules stay in your list as inactive entries.

**Need to inspect a path?** Operators can use `/safepaths inspect` to see progress, the selected speed, decay, and protection status. The command and its messages also work for clients without Safepaths installed.

[All settings](https://github.com/ahm3tak1f616/Safepaths/wiki/Configuration-Guide) · [Conversion editor](https://github.com/ahm3tak1f616/Safepaths/wiki/In-Game-Configuration-and-GUI) · [Inspect command](https://github.com/ahm3tak1f616/Safepaths/wiki/Inspect-Command)

## Compatibility

Safepaths works on its own. These integrations are optional:

| Mod | What Safepaths adds |
| --- | --- |
| **MineColonies** | Citizen-made paths and colony permission checks. |
| **Oh The Biomes We've Gone** | Default conversions for selected lush and sandy soils. |
| **Biomes O' Plenty** | A default conversion for origin grass. |
| **Regions Unexplored** | Default conversions for peat, silt, and chalk soils. |

Other soils and road materials can be added through the conversion editor or [datapack tags](https://github.com/ahm3tak1f616/Safepaths/wiki/Block-and-Entity-Tags). Each optional mod must match your Minecraft version and loader.

## Help and support

[**Report a bug**](https://github.com/ahm3tak1f616/Safepaths/issues) with your Minecraft, loader, and Safepaths versions, reproduction steps, and a relevant log or screenshot.

[Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki) · [Changelog](CHANGELOG.md) · [Support development on Patreon](https://www.patreon.com/ahmetakif616)

**Modpacks are welcome with credit.** All Rights Reserved; standalone redistribution is not permitted. See [LICENSE](LICENSE).

## Development

### Build from source

Install **JDK 17 and JDK 21**, and run the root wrapper with **Java 21**. In IntelliJ, set the root Gradle JVM to Java 21. The build selects Java 17 for Forge and Java 21 for NeoForge.

**Windows / PowerShell**

```powershell
git clone https://github.com/ahm3tak1f616/Safepaths.git
cd Safepaths
.\gradlew.bat build --console=plain
```

**Linux / macOS**

```bash
git clone https://github.com/ahm3tak1f616/Safepaths.git
cd Safepaths
./gradlew build --console=plain
```

The first build downloads Minecraft and the development dependencies. `build` compiles both editions, runs their GameTests, verifies the jars, and puts the **two production downloads** in the root `build/libs/`:

```text
build/libs/
├── safepaths-2.5.0-1.20.1-forge.jar
└── safepaths-2.5.0-1.21.1-neoforge.jar
```

Use these jars in Minecraft. An edition's `-sources.jar` or `-javadoc.jar` is a developer artifact, not an installable mod.

<details>
<summary><strong>More build commands</strong></summary>

Run from the repository root. On Linux and macOS, replace `.\gradlew.bat` with `./gradlew`.

```powershell
# Build and collect both jars without running GameTests
.\gradlew.bat assemble --console=plain

# Build only one edition; output stays in that edition's build/libs/
.\gradlew.bat buildForge --console=plain
.\gradlew.bat buildNeoForge --console=plain

# Test both editions without the optional MineColonies runtime
.\gradlew.bat runGameTestServer -PwithoutMineColonies --console=plain

# Show detailed Gradle deprecation warnings
.\gradlew.bat build --warning-mode=all --console=plain

# Enable detailed Minecraft and loader logs for GameTests
.\gradlew.bat runGameTestServer -PmodLogLevel=debug --console=plain
```

</details>

**Where to edit:** shared behavior and resources live in `common/`. Loader events, screens, and API adapters live in `forge-1.20.1/` and `neoforge-1.21.1/`. Both are maintained on `main`, with one `mod_version` in the root `gradle.properties`.

[Development guide](docs/DEVELOPMENT.md) · [Publish both jars in one release](docs/RELEASING.md)
