# Compatibility

## MineColonies

MineColonies is optional. Citizens, visitors, and mercenaries are included as path creators when their entity types are installed. They can receive road speed bonuses.

On claimed ground, players need break and place permissions to create or refresh paths. Civilian path creators must belong to the colony that owns the ground. Decay checks the recorded creator's permissions or colony ownership. Revoked permissions or a change of ownership can pause restoration.

Farmland, block entities, and blocks with a `minecolonies` ID are protected from trampling. Barbarians and entities in `#safepaths:path_blocked` do not create paths.

## Included biome conversions

Defaults cover the following block IDs when both source and target exist in the installed mods. Unavailable IDs are ignored.

| Mod | Ground | Path |
| --- | --- | --- |
| Oh The Biomes We've Gone | `lush_grass_block`, `lush_dirt` | `lush_dirt_path` |
| Oh The Biomes We've Gone | `sandy_dirt` | `sandy_dirt_path` |
| Biomes O' Plenty | `origin_grass_block` | `minecraft:dirt_path` |
| Regions Unexplored | `peat_grass_block`, `peat_dirt` | `peat_dirt_path` |
| Regions Unexplored | `silt_grass_block`, `silt_dirt` | `silt_dirt_path` |
| Regions Unexplored | `chalk_grass_block`, `chalk_dirt` | `chalk_dirt_path` |

Use companion mod versions for the same Minecraft version and loader. These defaults do not guarantee that every listed biome mod provides the same blocks on both editions.

## Other terrain mods

Terrain mods that place supported vanilla ground, including Terralith and Geophilic, use those ground rules. Other blocks need a conversion or a pathable tag; a soil-like name alone does not make a block eligible.

A modded shovel action is not automatically discovered. Safepaths reads Minecraft's shovel conversion table, then its fallback for eligible pathable blocks. Add an explicit rule when needed.

[Conversion editor](In-Game-Configuration-and-GUI) · [Datapack tags](Block-and-Entity-Tags)
