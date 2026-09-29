# 🌿 Safepaths (MineColonies)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg?logo=minecraft)](https://www.minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.235-orange.svg)](https://neoforged.net/)
[![License](https://img.shields.io/badge/License-All_Rights_Reserved-red.svg)](LICENSE)
[![GitHub Release](https://img.shields.io/badge/Version-1.2.0-blue.svg)](https://github.com/ahm3tak1f616/Safepaths/releases)

Safepaths brings natural path formation to your Minecraft world. Walking over the same blocks repeatedly will gradually trample them into dirt paths. Established paths reward players and friendly MineColonies NPCs with a configurable movement speed boost, making travel through your base or town faster and more immersive.

## ✨ Features
* 🚶 **Organic Path Creation:** Walk over the same blocks multiple times to naturally form dirt paths.
* ⚡ **Speed Boost:** Walking on paths grants a smooth movement speed bonus (default matches Speed I: +20%) with a grace period over slabs, stairs, and 1-block gaps so your camera doesn't jitter.
* ⏳ **Smart Decay:** Unused paths slowly revert back to their original ground blocks over time.
* 🛡️ **Farmland & Building Protection:** Farmland, `#safepaths:cannot_become_path`, and all MineColonies blocks are strictly protected from pathing.
* 🏛️ **MineColonies Support:** Colonists, visitors, and guards in `#safepaths:path_creators` naturally form paths during daily work and get the path speed boost. Hostiles like barbarians (`#safepaths:path_blocked`) are excluded.
* 🌐 **Multi-Language:** Available in 9 languages (English, German, Spanish, French, Japanese, Russian, Simplified Chinese, Turkish, and Brazilian Portuguese).

## 🧩 Dependencies
| Dependency | Type | Version Range | Side | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **NeoForge** | **Required** | `[21.1.235, )` | Both | Mod loader |
| **Minecraft** | **Required** | `[1.21.1]` | Both | Game engine |
| **MineColonies** | **Optional** | `[0, )` | Both | Adds citizen path-making, colony speed boost, & structure protection |

## ⚙️ Configuration
Open **Mods → Safepaths → Config** in-game, or edit `config/safepaths-common.toml`:
* `requiredPasses` (default `30`): Steps required to turn a block into a path.
* `enableSpeedBoost` (default `true`): Toggle the path movement speed bonus.
* `speedMultiplier` (default `0.2`): Speed bonus intensity (+20% ≈ Speed I).
* `decayTime` (default `2400`): Seconds before an unused path reverts to its original block.
* `constructionTime` (default `60`): Seconds before incomplete step memory resets.

## 🏷️ Datapack Tags
Pack makers can customize behavior using tags without touching code:

| Tag | Type | Purpose |
| :--- | :--- | :--- |
| `#safepaths:can_become_path` | Block | Blocks that can be trampled into dirt paths |
| `#safepaths:cannot_become_path` | Block | Blocks that must never become paths |
| `#safepaths:path_creators` | Entity | Non-player entities that can create paths and get speed |
| `#safepaths:path_blocked` | Entity | Entities excluded from using the system |

## 📦 Installation
1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1.
2. Put `safepaths-[version].jar` in your `.minecraft/mods` folder.
3. *(Optional)* Add [MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies).

*Note: Safepaths is server-side authoritative. It works in singleplayer and on multiplayer servers.*

## 🛠️ Development
Run automated GameTests locally:
```bash
./gradlew runGameTestServer
```

## 📜 Changelog
See [CHANGELOG.md](CHANGELOG.md) for full release notes and version history.

## 🔒 License / Permissions
**All Rights Reserved** — see [LICENSE](LICENSE).

* You may use the unmodified mod in singleplayer and on servers.
* **Modpacks:** You are free to include this mod in any public or private modpack without asking, provided that proper credit and a link to the original project are given.
* **Redistribution:** Do not re-upload the standalone mod jar to third-party sites or distribute modified builds without prior permission.
* Contact: GitHub issue or profile for [ahm3tak1f616/Safepaths](https://github.com/ahm3tak1f616/Safepaths).
