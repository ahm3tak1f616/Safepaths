# 📝 Configuration Reference Guide

Safepaths is fully customizable via its server and client configuration file located at:
`config/safepaths-common.toml`

This guide explains all configuration options, valid numerical ranges, conversion syntax, and ready-to-use tuning presets.

---

## ⚙️ Complete `safepaths-common.toml` Example

```toml
[Path Settings]
    # Time limit to perform required steps before the progress resets. (24,000 ticks = 1 Minecraft day).
    # Range: 1000 ~ 72000
    constructionTime = 24000

    # Number of steps to create a path (e.g., 20)
    # Range: 1 ~ 1000
    requiredPasses = 20

    # Time in ticks for an unused path to revert (72,000 ticks = 3 Minecraft days).
    # Range: 1000 ~ 240000
    decayTime = 72000

    # Enable or disable movement speed boost on paths.
    enableSpeedBoost = true

    # Movement speed boost percentage on paths (e.g., 0.2 = +20% speed).
    # Range: 0.0 ~ 5.0
    speedMultiplier = 0.2

    # Custom block conversions in format: source_block -> target_block
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

---

## 🔍 Property Details

### `constructionTime`
- **Type**: Integer
- **Default**: `24000` (1 full Minecraft day)
- **Range**: `1000` to `72000` ticks
- **Description**: The rolling time window during which footsteps are accumulated. If a block receives fewer than `requiredPasses` footsteps within this time window and is subsequently left abandoned, the accumulated step counter expires and resets to 0.

### `requiredPasses`
- **Type**: Integer
- **Default**: `20`
- **Range**: `1` to `1000`
- **Description**: The total number of footsteps required to compact the source block into its path form. Only steps crossing into the block from an adjacent position increment this counter.

### `decayTime`
- **Type**: Integer
- **Default**: `72000` (3 full Minecraft days)
- **Range**: `1000` to `240000` ticks (up to 10 in-game days)
- **Description**: Duration of inactivity required for an untraveled path to decay and revert back to its authentic original block state (e.g. back to grass, podzol, or modded dirt). Walking on the path at any point resets this countdown.

### `enableSpeedBoost`
- **Type**: Boolean
- **Default**: `true`
- **Description**: Global switch enabling or disabling the movement speed modifier for entities walking on recognized paths.

### `speedMultiplier`
- **Type**: Double / Float
- **Default**: `0.2` (+20% bonus speed)
- **Range**: `0.0` to `5.0`
- **Description**: Movement speed multiplier applied to entities on paths. A value of `0.2` translates to a 20% speed increase. Supports international decimal separators (`0.2` and `0,2`).

### `customConversions`
- **Type**: List of Strings
- **Format**: `"namespace:source_block -> namespace:target_block"`
- **Description**: Mapping rules that dictate what each block transforms into when trampled. Mapping a block to itself (`"block -> block"`) marks it as a permanent speed roadway that grants speed bonuses without converting or decaying.

---

## ⏱️ Useful Tick Conversion Reference

| Ticks | Real-World Time | In-Game Time |
| :--- | :--- | :--- |
| `20` | 1 second | - |
| `1,200` | 1 minute | 1 hour (in-game) |
| `24,000` | 20 minutes | 1 full Minecraft day (day + night) |
| `48,000` | 40 minutes | 2 Minecraft days |
| `72,000` | 1 hour | 3 Minecraft days |
| `120,000` | 1 hour 40 mins | 5 Minecraft days |
| `240,000` | 3 hours 20 mins | 10 Minecraft days |

---

## 💡 Configuration Presets

### 1. Fast-Paced / Quick Trails
*Great for adventure packs where players want trails to appear rapidly.*
```toml
[Path Settings]
constructionTime = 12000
requiredPasses = 8
decayTime = 48000
enableSpeedBoost = true
speedMultiplier = 0.25
```

### 2. High-Traffic / Hardened Civilizations
*Best for long-term survival servers or heavy MineColonies playthroughs where paths should require deliberate repeated traffic and last a long time.*
```toml
[Path Settings]
constructionTime = 48000
requiredPasses = 40
decayTime = 240000
enableSpeedBoost = true
speedMultiplier = 0.20
```

### 3. Builder's Speed Highways
*Ideal for city builders wanting extra speed on paved roads without paths appearing too easily in the wild.*
```toml
[Path Settings]
constructionTime = 24000
requiredPasses = 30
decayTime = 72000
enableSpeedBoost = true
speedMultiplier = 0.35
customConversions = [
    "minecraft:grass_block -> minecraft:dirt_path",
    "minecraft:dirt -> minecraft:dirt_path",
    "minecraft:gravel -> minecraft:gravel",
    "minecraft:stone_bricks -> minecraft:stone_bricks",
    "minecraft:cobblestone -> minecraft:cobblestone",
    "minecraft:smooth_stone -> minecraft:smooth_stone"
]
```
