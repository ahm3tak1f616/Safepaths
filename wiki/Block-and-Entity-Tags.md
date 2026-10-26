# 🏷️ Block & Entity Tags

Safepaths is built with full Minecraft datapack support. Modpack creators, server administrators, and developers can customize which blocks can become paths, which blocks are protected, and which entities can create paths using standard JSON tags.

---

## 🧱 Block Tags

### 1. `#safepaths:can_become_path`
- **Location**: `data/safepaths/tags/block/can_become_path.json`
- **Purpose**: Defines which natural ground blocks can be compacted into paths through repeated foot traffic.
- **Default Values**:
  - `minecraft:grass_block`
  - `minecraft:dirt`
  - `minecraft:podzol`
  - `minecraft:coarse_dirt`
  - `minecraft:rooted_dirt`
  - `minecraft:moss_block`
  - `minecraft:mycelium`
  - `minecraft:mud`
  - `minecraft:red_sand`
  - `minecraft:sand`
  - `minecraft:gravel`

### 2. `#safepaths:cannot_become_path`
- **Location**: `data/safepaths/tags/block/cannot_become_path.json`
- **Purpose**: Strict protection blacklist. Blocks in this tag will **never** be trampled into paths, regardless of foot traffic or other rules.
- **Default Values**:
  - `minecraft:farmland`

### 3. `#safepaths:is_path`
- **Location**: `data/safepaths/tags/block/is_path.json`
- **Purpose**: Identifies blocks that are treated as official paths. Walking on any block in this tag:
  1. Grants the movement speed boost (if enabled).
  2. Refreshes the decay timer so active paths do not revert.
- **Default Values**:
  - `minecraft:dirt_path`
- *Note*: Blocks ending in `_path`, winning conversion targets, and valid self-mapped road blocks are also recognized as paths. Source-only rules and targets discarded by duplicate resolution do not grant road speed.

Block entities, farmland, and MineColonies blocks remain protected from trampling regardless of pathable tags. Tags do not bypass claim permissions, target safety checks, or required ground contact.

---

## 👥 Entity Tags

Safepaths checks entity types to determine whether an entity is permitted to compact blocks into paths.

> [!NOTE]
> All players (except players in spectator mode) can create paths by default.

### 1. `#safepaths:path_creators`
- **Location**: `data/safepaths/tags/entity_type/path_creators.json`
- **Purpose**: Whitelist of non-player living entities permitted to create paths.
- **Default Values**:
  - `minecolonies:citizen` (optional)
  - `minecolonies:visitor` (optional)
  - `minecolonies:mercenary` (optional)

*Use case*: Add vanilla villagers (`minecraft:villager`) or custom pack companions here if you want them to leave trails behind as they wander!

### 2. `#safepaths:path_blocked`
- **Location**: `data/safepaths/tags/entity_type/path_blocked.json`
- **Purpose**: Blacklist of entities explicitly forbidden from ever creating paths.
- **Default Values**:
  - `minecolonies:barbarian` (optional)
  - `minecolonies:archerbarbarian` (optional)
  - `minecolonies:chiefbarbarian` (optional)

*Use case*: Add raid mobs, zombies, or wandering traders here if you want them barred from compacting terrain.

---

## 🛠️ Datapack Tutorial: Customizing Tags

To add custom blocks or entities to Safepaths tags using a datapack:

### 1. Directory Structure
```
my_custom_datapack/
├── pack.mcmeta
└── data/
    └── safepaths/
        └── tags/
            ├── block/
            │   ├── can_become_path.json
            │   └── cannot_become_path.json
            └── entity_type/
                └── path_creators.json
```

### 2. Example: `pack.mcmeta`
```json
{
  "pack": {
    "pack_format": 48,
    "description": "Custom Safepaths configuration"
  }
}
```

### 3. Example: Adding Villagers as Path Creators
`data/safepaths/tags/entity_type/path_creators.json`:
```json
{
  "replace": false,
  "values": [
    "minecraft:villager"
  ]
}
```

### 4. Example: Protecting Custom Garden Blocks
`data/safepaths/tags/block/cannot_become_path.json`:
```json
{
  "replace": false,
  "values": [
    "farmersdelight:rich_soil_farmland",
    "minecraft:clay"
  ]
}
```
