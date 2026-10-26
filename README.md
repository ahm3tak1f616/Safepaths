# 🌿 Safepaths (MineColonies)

<p align="center">
  <img src="src/main/resources/icon.png" alt="Safepaths Logo" width="128" height="128">
</p>

<p align="center">
  <a href="https://www.patreon.com/ahmetakif616"><img src="https://img.shields.io/badge/Patreon-Support_Me-FF424D?style=flat-square&logo=patreon&logoColor=white" alt="Patreon"></a>
  <a href="https://www.minecraft.net/"><img src="https://img.shields.io/badge/Minecraft-1.21.1-228B22?style=flat-square&logo=minecraft&logoColor=white" alt="Minecraft"></a>
  <a href="https://neoforged.net/"><img src="https://img.shields.io/badge/NeoForge-21.1.235+-E06622?style=flat-square" alt="NeoForge"></a>
  <a href="https://github.com/ahm3tak1f616/Safepaths/wiki"><img src="https://img.shields.io/badge/Wiki-Documentation-097979?style=flat-square&logo=github&logoColor=white" alt="Wiki"></a>
  <a href="https://github.com/ahm3tak1f616/Safepaths/releases"><img src="https://img.shields.io/badge/Version-2.0.1-097979?style=flat-square" alt="Version"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-All_Rights_Reserved-555555?style=flat-square" alt="License"></a>
</p>

<p align="center">
  <b>Natural, immersive path generation for your Minecraft world.</b><br>
  Repeatedly walk over ground blocks to trample them into paths with custom speed boosts.
</p>

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

Comprehensive documentation is hosted on the **[Safepaths GitHub Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki)** and maintained directly in the [`wiki/`](wiki/) directory:

| Guide | Description |
| :--- | :--- |
| 🚀 **[Getting Started](wiki/Getting-Started.md)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Getting-Started)) | Prerequisites, singleplayer & dedicated server installation, first steps. |
| ⚙️ **[Core Mechanics](wiki/Core-Mechanics.md)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Core-Mechanics)) | Step tracking (`getOnPos`), construction time windows, decay restoration, chunk persistence. |
| ⚡ **[Speed Boosts & Roadways](wiki/Speed-Boosts-and-Roadways.md)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Speed-Boosts-and-Roadways)) | Speed modifiers, 15-tick smoothing grace window, self-mapping speed roadways (`A -> A`). |
| 🎨 **[In-Game GUI & Palette](wiki/In-Game-Configuration-and-GUI.md)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/In-Game-Configuration-and-GUI)) | In-game conversion editor, live search, visual block palette, international decimal support. |
| 📝 **[Configuration Reference](wiki/Configuration-Guide.md)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Configuration-Guide)) | Full property definitions for `safepaths-common.toml`, tick conversions, and tuning presets. |
| 🏷️ **[Block & Entity Tags](wiki/Block-and-Entity-Tags.md)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Block-and-Entity-Tags)) | Datapack tags for pathable blocks, protected areas, and NPC path creator entities. |
| 🌐 **[Compatibility & Integrations](wiki/Compatibility-and-Integrations.md)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/Compatibility-and-Integrations)) | Integration with MineColonies, biomes mods (*BWG*, *BOP*, *RU*), *Terralith*, and *Geophilic*. |
| ❓ **[FAQ & Troubleshooting](wiki/FAQ-and-Troubleshooting.md)** ([Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki/FAQ-and-Troubleshooting)) | Performance considerations, chunk loading behavior, common questions, bug reports. |

---

### ⚙️ Configuration

Access in-game via **Mods ➔ Safepaths ➔ Config**, or edit `config/safepaths-common.toml`:

On a dedicated multiplayer server, the server owner edits this file on the server. The client config screen explains this instead of applying local changes that cannot affect the server.

```toml
[Path Settings]
# Steps required to compress ground into a path
requiredPasses = 20

# Time window (in ticks) to perform steps before progress resets
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

*For more details on tuning and preset examples, see the [Configuration Guide](wiki/Configuration-Guide.md).*

Air variants, liquids, non-solid blocks, and block entities cannot be conversion targets. Existing block entities are protected from trampling. Conversions and decay wait when their result would fall away or cannot survive at the position, including roads directly above water.

### Development checks

Run `gradlew.bat build runGameTestServer --console=plain` on Windows, or `./gradlew build runGameTestServer --console=plain` elsewhere. Add `-PwithoutMineColonies` to test without the optional MineColonies runtime and its dependencies. CI and publishing run the GameTests before accepting a build.

---

### 📦 Installation

1. Install [NeoForge 21.1.235+](https://neoforged.net/) for **Minecraft 1.21.1**.
2. Place `safepaths-x.x.x.jar` into your `.minecraft/mods` directory.
3. *(Optional)* Install [MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies) or any biome mod.

*See the [Getting Started Guide](wiki/Getting-Started.md) for dedicated server setup instructions.*

---

### 📜 Changelog
Check [CHANGELOG.md](CHANGELOG.md) for full update history.

### 🔒 Permissions
**All Rights Reserved** — Free to include in public or private modpacks with credit. Standalone redistributions prohibited.
