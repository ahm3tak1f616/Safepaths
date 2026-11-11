# Configuration

Safepaths 2.5.0 uses `config/safepaths-common.toml`. On a dedicated server, edit the server's file and restart. In singleplayer, use **Mods → Safepaths → Config** or close the game before editing the file.

## Settings

| Setting | Default | Range | Effect |
| --- | --- | --- | --- |
| `enablePathCreation` | `true` | true / false | Allow new footstep conversions. Existing roads still receive authorized traffic when disabled. |
| `sneakPreventsPathCreation` | `false` | true / false | Sneaking players add no construction passes. Existing roads still receive traffic. |
| `requiredPasses` | `20` | 1–1,000 | Counted passes needed to form a path. |
| `constructionTime` | `24000` | 1,000–72,000 ticks | Fixed window from the first counted pass. Later passes do not extend it. |
| `enablePathDecay` | `true` | true / false | Restore unused tracked roads. Disabling retains records and elapsed time. |
| `decayTime` | `72000` | 1,000–240,000 ticks | Time without authorized traffic before restoration. |
| `enableSpeedBoost` | `true` | true / false | Master switch for global and per-rule road speed bonuses. |
| `speedMultiplier` | `0.2` | 0.0–5.0 | Global bonus as a fraction; `0.2` means +20%. |
| `customConversions` | Built-in rules | List of strings | Conversions and permanent speed roads, with optional speed overrides. |

At normal tick speed, 20 ticks is one second, 24,000 ticks is one Minecraft day, and 72,000 ticks is three days. Restoration is checked once per second. Re-enabling decay can restore expired roads on the next maintenance pass because disabling it does not pause elapsed time.

```toml
["Path Settings"]
enablePathCreation = true
enablePathDecay = true
sneakPreventsPathCreation = false
requiredPasses = 20
constructionTime = 24000
decayTime = 72000
enableSpeedBoost = true
speedMultiplier = 0.2
```

## Conversion syntax and units

Use `namespace:source -> namespace:target`, optionally followed by `@ speed`:

```toml
["Path Settings"]
customConversions = [
    "minecraft:grass_block -> minecraft:dirt_path @ 0.1",
    "minecraft:dirt -> minecraft:dirt_path",
    "minecraft:sand -> minecraft:dirt_path @ 0.25",
    "minecraft:gravel -> minecraft:gravel",
    "minecraft:stone_bricks -> minecraft:stone_bricks @ 0.125"
]
```

This example gives grass-origin paths +10%, dirt-origin paths the global bonus, sand-origin paths +25%, and stone-brick roads +12.5%. Gravel inherits the global bonus. Use `@ 0` to disable a rule's bonus; omit `@` to inherit. A bare trailing `@` is invalid.

**The GUI uses percentages; TOML uses fractions.** Enter `12.5` or `12,5` in a GUI speed box for +12.5%; write `0.125` in TOML. The GUI range is 0–500%, corresponding to 0.0–5.0 in the file.

Replacing `customConversions` replaces the whole configured list. Keep any rules you want, including biome conversions. Missing-mod rules are retained in the editor but inactive at runtime. The [compatibility page](Compatibility-and-Integrations) lists the modded defaults.

## Priority and safety

A source mapped to itself is a permanent speed road. For manual duplicate source entries, an explicit conversion to a different block overrides a self-mapping; among explicit conversions, the first applicable rule wins. The GUI prevents duplicate sources.

Tracked roads use their saved original terrain to select a speed rule. Untracked roads use the first applicable rule targeting that road block. An explicit self-mapping for the road material overrides incoming target rules for speed, including on tracked roads. See [Road speed](Speed-Boosts-and-Roadways).

Air, liquids, non-solid blocks, and blocks with block-entity data are invalid targets. Protected terrain cannot be trampled, and unsupported placements leave the ground unchanged.

## Example: gentler path creation

```toml
["Path Settings"]
enablePathCreation = true
enablePathDecay = true
sneakPreventsPathCreation = true
requiredPasses = 8
constructionTime = 12000
decayTime = 48000
speedMultiplier = 0.25
```

This needs eight passes within half a Minecraft day, preserves new ground under sneaking players, and restores unused tracked paths after two days. Rules without a speed override receive +25%. Settings not shown retain their configured values.

## Saving and updating

In the GUI, the main Done saves settings and conversions together; Cancel discards the whole draft. Defaults restores the config specification's defaults after confirmation and waits for Done. File settings remain fractional even though speed fields display percentages.

Existing configuration files and path records remain supported. New switches take their defaults, and older conversion strings without a speed keep inheriting the global bonus. Back up the world before updating or removing terrain mods.
