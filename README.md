# 🌿 Safepaths (MineColonies)

<p align="center">
  <img src="common/src/main/resources/icon.png" alt="Safepaths Logo" width="128" height="128">
</p>

<p align="center">
  <a href="https://www.patreon.com/ahmetakif616"><img src="https://img.shields.io/badge/Patreon-Support_Me-FF424D?style=flat-square&logo=patreon&logoColor=white" alt="Patreon"></a>
  <a href="https://www.minecraft.net/"><img src="https://img.shields.io/badge/Minecraft-1.21.1%20%7C%201.20.1-228B22?style=flat-square&logo=minecraft&logoColor=white" alt="Minecraft"></a>
  <a href="https://neoforged.net/"><img src="https://img.shields.io/badge/NeoForge-21.1.235+-E06622?style=flat-square" alt="NeoForge"></a>
  <a href="https://files.minecraftforge.net/"><img src="https://img.shields.io/badge/Forge-47.3.0+-E06622?style=flat-square" alt="Forge"></a>
  <a href="https://github.com/ahm3tak1f616/Safepaths/wiki"><img src="https://img.shields.io/badge/Wiki-Documentation-097979?style=flat-square&logo=github&logoColor=white" alt="Wiki"></a>
  <a href="https://github.com/ahm3tak1f616/Safepaths/releases"><img src="https://img.shields.io/badge/Version-2.0.2-097979?style=flat-square" alt="Version"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-All_Rights_Reserved-555555?style=flat-square" alt="License"></a>
</p>

<p align="center">
  <b>Natural, immersive path generation for your Minecraft world.</b><br>
  Repeatedly walk over ground blocks to trample them into paths with custom speed boosts.
</p>

### Supported editions

| Minecraft | Loader | Java | Release file |
| :--- | :--- | :--- | :--- |
| 1.21.1 | NeoForge 21.1.235+ | 21 | `safepaths-2.0.2-1.21.1-neoforge.jar` |
| 1.20.1 | Forge 47.3.0+ | 17 | `safepaths-2.0.2-1.20.1-forge.jar` |

**2.0.2 — Stability** prevents holes caused by restoring unsupported falling terrain, including paths directly over water. It does not reconstruct blocks already missing.

---

### ✨ Features

| Feature | Description |
| :--- | :--- |
| 🚶 **Organic Pathing** | Blocks naturally compress into paths the more you walk over them. |
| ⚡ **Speed-Only Road Blocks** | Self-map blocks (`A -> A`, like stone bricks or gravel) to grant speed boosts without alteration or decay. |
| 🔘 **Master Speed Switch & Multiplier** | Toggle path speed and adjust its global multiplier directly inside the in-game conversion menu. |
| 🎨 **Visual Palette GUI** | In-game visual editor with live search, animated block selection, and smart filters. |
| ⏳ **Smart Decay** | Inactive paths gradually revert to their original block; active paths refresh on traffic. |
| 🛡️ **Colony Protection** | Protects farmland, block entities, and colony blocks; checks MineColonies permissions before changing claimed ground. |
| 🏛️ **MineColonies Support** | Citizens form paths during daily work routines and benefit from speed boosts. |
| 🌍 **Mod Compatibility** | Built-in defaults for vanilla soils, *BWG*, *BOP*, and *Regions Unexplored*, plus the supported vanilla terrain blocks used by *Terralith* and *Geophilic*. |
| 🌐 **9 Languages** | English, German, Spanish, French, Japanese, Russian, Chinese, Turkish, Portuguese. |

---

### 📖 Documentation & Wiki

Comprehensive documentation is hosted on the **[Safepaths GitHub Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki)** and maintained in the [`wiki/`](https://github.com/ahm3tak1f616/Safepaths/tree/main/wiki) directory on `main` for both editions:

| Guide | Description |
| :--- | :--- |
| 🚀 **[Getting Started](https://github.com/ahm3tak1f616/Safepaths/wiki/Getting-Started)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Getting-Started)) | Prerequisites, singleplayer & dedicated server installation, first steps. |
| ⚙️ **[Core Mechanics](https://github.com/ahm3tak1f616/Safepaths/wiki/Core-Mechanics)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Core-Mechanics)) | Step tracking (`getOnPos`), construction time windows, decay restoration, chunk persistence. |
| ⚡ **[Speed Boosts & Roadways](https://github.com/ahm3tak1f616/Safepaths/wiki/Speed-Boosts-and-Roadways)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Speed-Boosts-and-Roadways)) | Speed modifiers, 15-tick smoothing grace window, self-mapping speed roadways (`A -> A`). |
| 🎨 **[In-Game GUI & Palette](https://github.com/ahm3tak1f616/Safepaths/wiki/In-Game-Configuration-and-GUI)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/In-Game-Configuration-and-GUI)) | In-game conversion editor, live search, visual block palette, international decimal support. |
| 📝 **[Configuration Reference](https://github.com/ahm3tak1f616/Safepaths/wiki/Configuration-Guide)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Configuration-Guide)) | Full property definitions for `safepaths-common.toml`, tick conversions, and tuning presets. |
| 🏷️ **[Block & Entity Tags](https://github.com/ahm3tak1f616/Safepaths/wiki/Block-and-Entity-Tags)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Block-and-Entity-Tags)) | Datapack tags for pathable blocks, protected areas, and NPC path creator entities. |
| 🌐 **[Compatibility & Integrations](https://github.com/ahm3tak1f616/Safepaths/wiki/Compatibility-and-Integrations)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Compatibility-and-Integrations)) | Integration with MineColonies, biomes mods (*BWG*, *BOP*, *RU*), *Terralith*, and *Geophilic*. |
| ❓ **[FAQ & Troubleshooting](https://github.com/ahm3tak1f616/Safepaths/wiki/FAQ-and-Troubleshooting)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/FAQ-and-Troubleshooting)) | Performance considerations, chunk loading behavior, common questions, bug reports. |

---

### ⚙️ Configuration

Access in-game via **Mods ➔ Safepaths ➔ Config**, or edit `config/safepaths-common.toml`:

On a dedicated multiplayer server, the server owner edits this file on the server. The client config screen explains this instead of applying local changes that cannot affect the server.

```toml
[Path Settings]
# Steps required to compress ground into a path
requiredPasses = 20

# Fixed time window (in ticks) starting with the first step before progress resets
constructionTime = 24000

# Ticks until an unused path reverts (72,000 = 3 days)
decayTime = 72000

# Master speed switch and multiplier (also editable in Conversions GUI)
enableSpeedBoost = true
speedMultiplier = 0.2

# Custom conversions: "source_block -> target_block"
# Hint: mapping a block to itself (e.g. gravel -> gravel) makes it a speed road without alteration
customConversions = [
    "minecraft:grass_block -> minecraft:dirt_path",
    "minecraft:dirt -> minecraft:dirt_path",
    "minecraft:sand -> minecraft:dirt_path",
    "minecraft:gravel -> minecraft:gravel",
    "biomeswevegone:lush_grass_block -> biomeswevegone:lush_dirt_path",
    "biomeswevegone:lush_dirt -> biomeswevegone:lush_dirt_path",
    "biomeswevegone:sandy_dirt -> biomeswevegone:sandy_dirt_path",
    "biomesoplenty:origin_grass_block -> minecraft:dirt_path",
    "regions_unexplored:peat_grass_block -> regions_unexplored:peat_dirt_path",
    "regions_unexplored:peat_dirt -> regions_unexplored:peat_dirt_path",
    "regions_unexplored:silt_grass_block -> regions_unexplored:silt_dirt_path",
    "regions_unexplored:silt_dirt -> regions_unexplored:silt_dirt_path",
    "regions_unexplored:chalk_grass_block -> regions_unexplored:chalk_dirt_path",
    "regions_unexplored:chalk_dirt -> regions_unexplored:chalk_dirt_path"
]
```

*For more details on tuning and preset examples, see the [Configuration Guide](https://github.com/ahm3tak1f616/Safepaths/wiki/Configuration-Guide).*

Air variants, liquids, non-solid blocks, and block entities cannot be conversion targets. Existing block entities are protected from trampling. Conversions and decay wait when their result would fall away or cannot survive at the position, including roads directly above water.

### Development checks

Both editions are maintained together in one checkout. Install JDK 17 and JDK 21; use Java 21 to run the root Gradle wrapper. The root build selects Java 17 for Forge and Java 21 for NeoForge (missing toolchains can also be provisioned by Gradle).

Run `gradlew.bat build --console=plain` on Windows, or `./gradlew build --console=plain` elsewhere. This builds both editions, runs both GameTest suites, and collects the two production JARs in `build/libs/`. Add `-PwithoutMineColonies` to repeat the tests without MineColonies and its runtime dependencies.

| Directory | Contents |
| :--- | :--- |
| `common/` | Path creation, decay, save format, MineColonies protection, default conversions, translations, and tag definitions. |
| `forge-1.20.1/` | Forge events, API adapters, configuration, GUI, and GameTests; Gradle 8.7 / Java 17. |
| `neoforge-1.21.1/` | NeoForge equivalents; Gradle 9.2.1 / Java 21. |

Shared Java sources compile separately against each target's Minecraft API. Forge resource processing translates the shared singular tag/structure directories to the plural names required by 1.20.1. Each edition includes the shared code and resources inside its own JAR; players install one matching JAR.

For one edition, run `gradlew.bat buildForge` or `gradlew.bat buildNeoForge` from the root. Run `testForge` or `testNeoForge` for that edition's build and GameTests. Individual artifacts are in the edition's `build/libs/`; `assemble` builds and collects both without GameTests. Root `clean` removes the collected JARs; run an edition's wrapper with `clean` to clear its own build output.

In IntelliJ, link both edition `build.gradle` files as Gradle projects and select each project's wrapper. Run client/server configurations from the desired edition with its matching JDK. Shared sources appear in both projects; edit the file in `common/` once. Mod version and identity are configured in the root `gradle.properties`; loader and dependency versions live in each edition's properties.

CI and CodeQL cover `main` and `shared-multiversion`. The reusable `build-editions.yml` builds and tests both editions with and without MineColonies, verifies the jars, and stores the two production files as one artifact. CI rebuilds location-dependent NeoGradle data and disables the Gradle build cache to avoid reusing paths from the previous layout.

Publishing a GitHub release tagged `v<mod_version>` runs the same validation once. `publish-modrinth.yml` and `publish-curseforge.yml` then upload each tested jar with its own Minecraft version, loader, Java version, and edition-specific version number. Both jars are also attached to that GitHub release. Pushes and pull requests only build and test; publishing runs on a published release. Project-page and wiki synchronization follow `main`, while release-page links point to the release tag.

---

### 📦 Installation

1. Install [NeoForge 21.1.235+](https://neoforged.net/) for **Minecraft 1.21.1**, or [Forge 47.3.0+](https://files.minecraftforge.net/) for **Minecraft 1.20.1**.
2. Download the matching release file from the table above and place it into your `.minecraft/mods` directory. Do not mix the two loader editions.
3. *(Optional)* Install [MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies) or a supported biome mod, using the same Minecraft version and loader.
4. When updating, back up your world, replace the old Safepaths JAR, and restart. Existing configuration and path-memory saves remain compatible.

*See the [Getting Started Guide](https://github.com/ahm3tak1f616/Safepaths/wiki/Getting-Started) for dedicated server setup instructions.*

---

### 📜 Changelog
Check [CHANGELOG.md](CHANGELOG.md) for full update history.

### 🔒 Permissions
**All Rights Reserved** — Free to include in public or private modpacks with credit. Standalone redistributions prohibited.
