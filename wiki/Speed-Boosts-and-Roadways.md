# Road speed

Recognized roads give living entities a movement speed bonus. The default `speedMultiplier` of `0.2` adds 20%; `0.5` adds 50%. Set `enableSpeedBoost` to `false` to disable the bonus.

Dirt paths, blocks in `#safepaths:is_path`, valid conversion targets, speed-only roads, and blocks whose IDs end in `_path` are recognized as roads.

## Permanent roads

Add a conversion from a road block to itself:

```toml
["Path Settings"]
customConversions = [
    "minecraft:gravel -> minecraft:gravel",
    "minecraft:stone_bricks -> minecraft:stone_bricks"
]
```

These roads receive the speed bonus without being changed or given a decay timer. Gravel has this rule by default. Editing the list replaces your configured conversions, so keep any other rules you want to use.

In the conversion editor, add a row and select the same source and target block.

## Movement

The bonus remains for up to 15 ticks after leaving a path to reduce rapid speed changes at edges and small gaps. It is removed in water, bubble columns, or lava; during elytra or player flight; in spectator mode; and when fall distance reaches 3.5 blocks.

The bonus is reapplied from current road checks after a world reload. It is not stored as a permanent entity attribute.
