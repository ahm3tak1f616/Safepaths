# Datapack tags

Use tags to choose eligible ground, protected blocks, recognized roads, and non-player path creators.

| Tag | Purpose | Default |
| --- | --- | --- |
| `#safepaths:can_become_path` | Eligible ground. | Grass block, dirt, podzol, coarse dirt, rooted dirt, moss, mycelium, mud, sand, red sand, gravel. |
| `#safepaths:cannot_become_path` | Prevent trampling and tracked decay. | Farmland. |
| `#safepaths:is_path` | Recognized roads that grant the configured speed bonus. | Dirt path. |
| `#safepaths:path_creators` | Non-player living entities that can create paths. | Optional MineColonies citizens, visitors, and mercenaries. |
| `#safepaths:path_blocked` | Non-player entities that cannot create paths. | Optional MineColonies barbarian types. |

Players are handled separately from the entity tags. Tagging a block as a road does not give it a decay timer; existing Safepaths records can be refreshed by authorized traffic. Safety checks, ground contact, and colony permissions still apply. Pathable tags do not override farmland, block-entity, or MineColonies-block protection.

## Version-specific paths

| Minecraft | Block tags | Entity tags | Datapack format |
| --- | --- | --- | --- |
| 1.21.1 | `data/safepaths/tags/block/` | `data/safepaths/tags/entity_type/` | 48 |
| 1.20.1 | `data/safepaths/tags/blocks/` | `data/safepaths/tags/entity_types/` | 15 |

Tag IDs are the same on both versions. The examples below use the 1.21.1 directories.

Create a datapack with this `pack.mcmeta`:

```json
{
  "pack": {
    "pack_format": 48,
    "description": "Safepaths custom tags"
  }
}
```

To allow villagers to create paths, add `data/safepaths/tags/entity_type/path_creators.json`:

```json
{
  "replace": false,
  "values": ["minecraft:villager"]
}
```

To protect clay, add `data/safepaths/tags/block/cannot_become_path.json`:

```json
{
  "replace": false,
  "values": ["minecraft:clay"]
}
```

Install the datapack in the world's `datapacks` folder and run `/reload`. Use `"replace": false` to retain the existing tag entries.
