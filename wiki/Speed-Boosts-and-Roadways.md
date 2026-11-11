# Road speed

Recognized roads give living entities a movement speed bonus. The global TOML `speedMultiplier` defaults to `0.2` (+20%); the GUI shows `20` with `%` outside the box. `enableSpeedBoost = false` disables every road bonus, including per-rule overrides.

Setting `speedMultiplier = 0` alone only removes the inherited global bonus; positive per-rule overrides still apply. Use the master switch to turn every bonus off. A value of `0.2` adds 20% (a 1.2× factor from Safepaths), rather than setting the total factor to 0.2×.

Dirt paths, blocks in `#safepaths:is_path`, valid conversion targets, permanent speed roads, and blocks whose IDs end in `_path` are recognized as roads.

## Speed per conversion

```toml
["Path Settings"]
customConversions = [
    "minecraft:grass_block -> minecraft:dirt_path @ 0.1",
    "minecraft:dirt -> minecraft:dirt_path",
    "minecraft:sand -> minecraft:dirt_path @ 0.25"
]
```

Grass-origin paths give +10%, dirt-origin paths inherit the global bonus, and sand-origin paths give +25%. Both grass and sand can become the same dirt-path block while retaining different bonuses: Safepaths uses the original ground saved in each tracked road record.

In the GUI, enter `10` or `25` instead of the TOML fractions. An empty rule speed inherits the global setting; `0` explicitly gives no bonus. Dot and comma decimal input are accepted. A speed override does not change the number of passes or construction window.

For an untracked road, such as a placed or shovel-made path, the original terrain is unknown. The first applicable conversion targeting its road material supplies the bonus. Roads with no applicable speed override use the global value.

## Permanent roads

Map a block to itself:

```toml
["Path Settings"]
customConversions = [
    "minecraft:gravel -> minecraft:gravel",
    "minecraft:stone_bricks -> minecraft:stone_bricks @ 0.125"
]
```

These roads receive speed without changing or acquiring a decay timer. Gravel has a self-mapping by default. Stone bricks in this example give +12.5%, while gravel inherits the global bonus. Editing this list replaces your configured conversions, so keep any other rules you want.

An explicit self-mapping for the road material takes precedence over incoming conversion rules when choosing its speed. For example, a dirt-path self-mapping sets one material-wide speed even if grass and sand conversions also target dirt paths.

## Movement and switches

Road bonuses can work while creation or decay is disabled. Authorized traffic still refreshes existing tracked roads. Sneaking prevention affects construction passes, not the road-speed switch.

The bonus remains for up to 15 ticks after leaving a road to reduce rapid changes at edges and small gaps. It is removed in water, bubble columns, or lava; during elytra or player flight; in spectator mode; and when fall distance reaches 3.5 blocks. An explicit zero-speed road removes a previous road bonus instead of inheriting it.

The bonus is reapplied from current road checks after a world reload. It is not stored as a permanent entity attribute. Use the [inspect command](Inspect-Command) to see the bonus chosen for a block; it reports the block's road bonus, not every movement restriction on an entity.
