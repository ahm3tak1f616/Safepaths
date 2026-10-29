# Configuration

Safepaths uses `config/safepaths-common.toml`. On a dedicated server, edit the server's file and restart. In singleplayer, use **Mods → Safepaths → Config** or close the world before editing the file.

## Settings

| Setting | Default | Range | Effect |
| --- | --- | --- | --- |
| `requiredPasses` | `20` | 1–1,000 | Counted passes needed to form a path. |
| `constructionTime` | `24000` | 1,000–72,000 ticks | Fixed window from the first counted step. |
| `decayTime` | `72000` | 1,000–240,000 ticks | Time without authorized traffic before a tracked path restores its original terrain. |
| `enableSpeedBoost` | `true` | true / false | Enable the road speed bonus. |
| `speedMultiplier` | `0.2` | 0.0–5.0 | Speed bonus; `0.2` means +20%. |
| `customConversions` | Built-in rules | List of strings | Ground-to-path conversions and speed-only roads. |

At normal tick speed, 20 ticks is one second, 24,000 ticks is one Minecraft day, and 72,000 ticks is three days. Later steps do not extend the construction window. Decay time is checked once per second.

```toml
["Path Settings"]
requiredPasses = 20
constructionTime = 24000
decayTime = 72000
enableSpeedBoost = true
speedMultiplier = 0.2
```

## Conversions

Use `namespace:source -> namespace:target` entries:

```toml
["Path Settings"]
customConversions = [
    "minecraft:grass_block -> minecraft:dirt_path",
    "minecraft:dirt -> minecraft:dirt_path",
    "minecraft:sand -> minecraft:dirt_path",
    "minecraft:gravel -> minecraft:gravel",
    "minecraft:stone_bricks -> minecraft:stone_bricks"
]
```

Replacing `customConversions` replaces the entire configured list. Keep the rules you still want, including any biome conversions. The [compatibility page](Compatibility-and-Integrations) lists the included modded-block defaults.

A block mapped to itself is a permanent speed road. An explicit conversion overrides self-mapping; among explicit conversions for the same source, the first wins. Air, liquids, non-solid blocks, and block entities are invalid targets. Unsupported targets leave the ground unchanged.

There is no global decay-disable setting. Increasing `decayTime` delays restoration; use self-mapped road blocks for permanent roads.

## Example: faster trails

```toml
["Path Settings"]
requiredPasses = 8
constructionTime = 12000
decayTime = 48000
speedMultiplier = 0.25
```

This needs eight passes within half a Minecraft day and restores an unused tracked path after two days. It leaves settings not shown here at their configured values.
