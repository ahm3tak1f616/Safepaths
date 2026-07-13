# Safepaths (Minecolonies)

Safepaths is a lightweight, standalone NeoForge mod that brings organic path formation to your Minecraft world. Walking over the same blocks repeatedly will gradually trample them into dirt paths. Established paths reward entities with a configurable movement speed boost, making travel through your base or village faster and more immersive.

## Features
* **Organic Path Creation:** Traverse the same block multiple times to naturally form a path. 
* **Speed Boost:** Walking on established paths grants a seamless, transient movement speed bonus (default matches Speed I: +20%).
* **Smart Decay:** Unused paths naturally revert to their original block over time, keeping the environment dynamic.
* **Farmland Protection:** Vanilla farmland, `#safepaths:cannot_become_path`, any block whose id contains `farmland`, and all MineColonies blocks are excluded from path formation.
* **MineColonies Integration (Optional):** Entities in `#safepaths:path_creators` (citizens, visitors, mercenaries by default) create paths and get the speed boost. `#safepaths:path_blocked` plus any entity id containing `barbarian` are excluded.

## Configuration
Open **Mods → Safepaths → Config**, or edit the common config file. Options include:
* `requiredPasses`: Number of steps required to turn a block into a path.
* `enableSpeedBoost`: Toggle the path movement speed bonus.
* `speedMultiplier`: Speed bonus intensity (`ADD_MULTIPLIED_TOTAL`; `0.2` ≈ Speed I).
* `decayTime`: How long an unused path takes to revert to its original block.
* `constructionTime`: Memory reset timer for incomplete path formation.

## Datapack tags
Pack makers can extend these tags without code changes (entries use `"required": false` where MineColonies may be absent):

| Tag | Purpose |
|-----|---------|
| `#safepaths:can_become_path` | Blocks that can be trampled into dirt paths |
| `#safepaths:cannot_become_path` | Extra blocks that must never become paths |
| `#safepaths:path_creators` | Non-player entities that create paths / get speed |
| `#safepaths:path_blocked` | Entities that are never allowed to use the system |

MineColonies *blocks* are still skipped by namespace in code so colony buildings stay protected even if not listed in a block tag.

## Installation
1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1.
2. Drop the `safepaths-[version].jar` file into your `.minecraft/mods` folder.

*Note: Safepaths operates purely through server-side logic. It works in both singleplayer worlds and multiplayer servers.*

## Development
* GameTests live in `SafePathsGameTests` (template `safepaths:platform`). Run with `./gradlew runGameTestServer`.

## License / permissions
**All Rights Reserved** — see [LICENSE](LICENSE).

* You may use the unmodified mod in singleplayer and on servers.
* **Do not put this mod in a modpack** (or redistribute / publish modified versions) **without contacting me first** and getting permission.
* Contact: GitHub issue or profile for [ahm3tak1f616/Safepaths](https://github.com/ahm3tak1f616/Safepaths).
