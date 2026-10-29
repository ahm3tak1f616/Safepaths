# Safepaths

<p align="center">
  <img src="common/src/main/resources/icon.png" alt="Safepaths" width="128" height="128">
</p>

Paths form where you walk. Safepaths turns frequently used ground into trails, gives roads a configurable speed boost, and restores unused paths to their original terrain.

Works on its own, with optional MineColonies support for citizen-made paths.

## Downloads

| Minecraft | Loader | Java | Choose the file ending in |
| --- | --- | --- | --- |
| 1.21.1 | NeoForge 21.1.235+ | 21 | `-1.21.1-neoforge.jar` |
| 1.20.1 | Forge 47.3.0+ | 17 | `-1.20.1-forge.jar` |

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/safepaths) · [Modrinth](https://modrinth.com/mod/safepaths) · [GitHub Releases](https://github.com/ahm3tak1f616/Safepaths/releases)

Install the jar for your Minecraft version and loader in the `mods` folder. When updating, back up your world and replace the previous Safepaths jar.

## How it works

- Walk a route repeatedly to turn grass, dirt, sand, and other supported ground into paths.
- Paths and gravel roads give a 20% movement speed bonus by default.
- Unused paths return to their original block after three Minecraft days by default. Placed dirt paths are not given a decay timer automatically.
- Choose which blocks become paths, set the number of passes, and adjust speed and decay in the config.
- MineColonies citizens can form paths. Claimed ground respects colony permissions; farmland, block entities, and MineColonies blocks are protected.

You need to move between blocks to count more footsteps. Standing still or jumping in place does not build a path. If restoring sand or gravel would make it fall, the path stays until it has support.

## Configuration

Open **Mods → Safepaths → Config**, or edit `config/safepaths-common.toml`.

On a dedicated server, the server owner edits the server's file. The client menu cannot change a remote server's settings.

```toml
["Path Settings"]
requiredPasses = 20
constructionTime = 24000
decayTime = 72000
enableSpeedBoost = true
speedMultiplier = 0.2
```

The time values use game ticks: 24,000 ticks is one Minecraft day at normal tick speed. The construction window starts with the first counted step.

To make a permanent speed road, add a conversion from a block to itself, such as `minecraft:stone_bricks -> minecraft:stone_bricks`. Gravel already has this rule by default. These blocks receive the road speed bonus without changing or decaying.

## Compatibility

MineColonies is optional. Included conversion rules also cover selected blocks from Oh The Biomes We've Gone, Biomes O' Plenty, and Regions Unexplored when those blocks are installed. Other soils can be added through the conversion editor or datapack tags.

[Installation](https://github.com/ahm3tak1f616/Safepaths/wiki/Getting-Started) · [Configuration](https://github.com/ahm3tak1f616/Safepaths/wiki/Configuration-Guide) · [Compatibility](https://github.com/ahm3tak1f616/Safepaths/wiki/Compatibility-and-Integrations) · [Changelog](CHANGELOG.md)

## Support

Report problems on [GitHub Issues](https://github.com/ahm3tak1f616/Safepaths/issues) with your Minecraft, loader, and Safepaths versions, steps to reproduce, and a log or screenshot when relevant.

[Support development on Patreon](https://www.patreon.com/ahmetakif616).

## Permissions

All Rights Reserved. You may include Safepaths in modpacks with credit. Standalone redistribution is not permitted; see [LICENSE](LICENSE).

## Development

[Build and test](docs/DEVELOPMENT.md) · [Release process](docs/RELEASING.md)
