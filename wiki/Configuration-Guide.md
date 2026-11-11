# Configuration

Safepaths uses `config/safepaths-common.toml`. In singleplayer, use **Mods → Safepaths → Config** or close the game before editing the file. On a dedicated server, edit the server's file and restart the server. A client's local file and menu cannot configure a remote server; `/reload` reloads datapacks, not this TOML file.

## Version scope

**This reference describes Safepaths 2.5.0.** Check the version installed in the Mods list or server log before copying settings. The separate creation/decay switches, sneaking option, and `@ speed` rule suffix require 2.5.0; older 2.0.x releases do not provide them. A newer wiki page does not mean an older installed jar has these features. Check the [release list](https://github.com/ahm3tak1f616/Safepaths/releases) for the files actually published.

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
| `customConversions` | 14 built-in entries shown below | List of strings | Conversions and permanent speed roads, with optional speed overrides. |

At normal tick speed, 20 ticks is one second, 24,000 ticks is one Minecraft day, and 72,000 ticks is three days. Restoration is checked once per second. Re-enabling decay can restore expired roads on the next maintenance pass because disabling it does not pause elapsed time.

Pass counts and tick durations are whole numbers. TOML numeric literals use a decimal point; the GUI also accepts a decimal comma. A `speedMultiplier` of `0.2` is a +20% bonus, equivalent to a 1.2× speed factor from Safepaths, rather than a total speed factor of 0.2×.

## Complete default configuration

The following is the full default configuration for both editions, including all conversion entries. Keep a single `["Path Settings"]` table in the file. Later examples are alternatives or partial edits: merge their keys into that existing table instead of appending another table with the same name.

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

The optional-mod entries are normal defaults even when those mods are absent. They stay in the list but are inactive until their blocks exist. Other eligible vanilla ground, including mud and moss, uses the built-in fallback conversion rules; it does not need to appear in this list.

An empty `customConversions = []` removes configured conversions and permanent-road rules. It does **not** disable all path creation: tagged eligible ground can still use fallback conversions. Use `enablePathCreation = false` to stop creation.

This also removes gravel's default self-mapping, so eligible gravel can then turn into a dirt path through the fallback rules. Keep the gravel self-mapping if you want it to remain a permanent gravel road.

## Conversion syntax and units

Use `namespace:source -> namespace:target`, optionally followed by `@ speed`. Use block registry IDs; these entries do not accept item IDs, tags such as `#safepaths:is_path`, or block-state expressions such as `[snowy=true]`. A conversion creates the target block's default state; a tracked road retains the original state for restoration.

The following replaces the default list with five example entries:

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

| Desired behavior | GUI | TOML |
| --- | --- | --- |
| Global +20% | Global speed `20` | `speedMultiplier = 0.2` |
| Rule inherits the global bonus | Leave rule speed empty | Omit `@ speed` |
| Rule gives +12.5% | Rule speed `12.5` or `12,5` | Append `@ 0.125` |
| One rule gives no bonus | Rule speed `0` | Append `@ 0` |
| Every road gives no bonus | Disable the road-speed switch | `enableSpeedBoost = false` |

Setting only `speedMultiplier = 0` silences rules that inherit it; positive per-rule overrides still work. The master switch is the way to disable all bonuses. In the TOML file use `0.125`, not `12.5`, for +12.5%. A fractional comma can occur inside a quoted rule string, but not as an unquoted TOML number; the examples consistently use dots.

Replacing `customConversions` replaces the whole configured list. Keep any rules you want, including biome conversions. Missing-mod rules are retained in the editor but inactive at runtime. The [compatibility page](Compatibility-and-Integrations) lists the modded defaults.

Invalid syntax, unsafe targets, and missing blocks make a rule inapplicable at runtime; an invalid entry does not become a working conversion just because it remains in the TOML list. The GUI reports validation problems and prevents saving invalid rules, while allowing missing-mod entries to be retained. The config loader may correct invalid setting types or out-of-range values to their defaults; check the server/client log when a manual edit does not stick.

## Priority and safety

A source mapped to itself is a permanent speed road. For manual duplicate source entries, an explicit conversion to a different block overrides a self-mapping; among explicit conversions, the first applicable rule wins. The GUI prevents duplicate sources.

Tracked roads use their saved original terrain to select a speed rule. Untracked roads use the first applicable rule targeting that road block. An explicit self-mapping for the road material overrides incoming target rules for speed, including on tracked roads. See [Road speed](Speed-Boosts-and-Roadways).

A valid conversion target is recognized as a road material everywhere, including placed or untracked copies of that block. Conversions are not a multi-stage chain: blocks already recognized as roads are not repeatedly trampled into another target. Consider this when using a block as both a source and another rule's target.

Air, liquids, non-solid blocks, and blocks with block-entity data are invalid targets. Protected terrain cannot be trampled, and unsupported placements leave the ground unchanged.

## What the switches change

- Disabling creation stops new construction passes. It does not remove existing roads or their speed bonuses. Eligible grounded path creators with the required permissions can still refresh tracked roads.
- Sneaking prevention applies to player construction passes. It does not disable road speed or prevent authorized refreshes of existing tracked roads.
- Disabling decay retains valid road records and elapsed time. Normal cleanup can still remove stale records after blocks change or a material becomes a permanent speed road. Re-enabling decay, or shortening `decayTime`, can make already-expired roads eligible for restoration on the next maintenance pass.
- Disabling speed removes bonuses without disabling creation or restoration.

Only roads tracked by Safepaths have a decay timer. Placed or shovel-made roads do not acquire one automatically. Restoration also waits for safe support and, when applicable, MineColonies permissions. Inspect a loaded block with [`/safepaths inspect`](Inspect-Command) to see its progress, selected speed, and restoration status.

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
