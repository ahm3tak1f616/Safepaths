# Getting started

## Install

| Minecraft | Loader | Java | Download ends with |
| --- | --- | --- | --- |
| 1.21.1 | NeoForge 21.1.235+ | 21 | `-1.21.1-neoforge.jar` |
| 1.20.1 | Forge 47.3.0+ | 17 | `-1.20.1-forge.jar` |

1. Install the loader for your Minecraft version.
2. Download the matching Safepaths jar from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/safepaths) or [Modrinth](https://modrinth.com/mod/safepaths).
3. Put the jar in your instance's `mods` folder and start the game.

MineColonies and biome mods are optional. Their versions must match your Minecraft version and loader.

On a dedicated server, install Safepaths in the server's `mods` folder. The server controls path creation, speed, and decay. A client installation provides the configuration menu; it cannot edit a remote server's settings.

## Try it

Walk back and forth along a short grass or dirt route. By default, a block becomes a path after 20 counted passes within one Minecraft day. Standing still or jumping in place does not count as repeated passes.

Dirt paths and gravel roads give a 20% speed bonus by default. A Safepaths-created path returns to its original terrain after three Minecraft days without authorized traffic. Placed or shovel-made paths are not automatically given a decay timer.

## Configure

Open **Mods → Safepaths → Config** from the loader's Mods menu. On a dedicated server, edit `config/safepaths-common.toml` on the server and restart it.

[Settings reference](Configuration-Guide) · [Conversion editor](In-Game-Configuration-and-GUI)

## Update

Back up your world, replace the old Safepaths jar with the matching new one, and restart. Existing configuration and path records remain readable. The safety fixes prevent new holes from unsupported falling terrain; they do not repair missing blocks from older versions.
