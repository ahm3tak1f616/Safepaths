# Troubleshooting

## Why is a block not becoming a path?

Move between blocks rather than standing or jumping in place. The passes must fit within the fixed `constructionTime` window that starts with the first counted step.

Check that the block has an active conversion or is in `#safepaths:can_become_path`. Farmland, block entities, MineColonies blocks, and `#safepaths:cannot_become_path` are protected. Claim permissions and target safety also apply. Non-player entities need `#safepaths:path_creators`; spectators and flying players do not add passes.

## Why is a path not decaying?

Only paths created and tracked by Safepaths have a decay timer. Placed or shovel-made paths are not automatically tracked. Valid traffic refreshes the timer, and speed-only roads have no decay timer.

Decay waits while a chunk is unloaded, the original block cannot survive, falling terrain lacks support, or MineColonies permissions prevent restoration.

## Can I make permanent roads?

Yes. Map the road block to itself, for example `minecraft:stone_bricks -> minecraft:stone_bricks`. Raising `decayTime` only delays decay; its maximum is ten Minecraft days.

## Where do I configure a server?

Edit `config/safepaths-common.toml` on the server and restart it. The client screen cannot configure a remote server. `/reload` reloads datapacks, not this TOML configuration.

## A road above water left a hole

Older versions could restore unsupported sand or gravel that then fell. The safety checks retain the path until restoration is supported. They do not rebuild blocks already missing.

If this happens on the current version, report the original terrain, conversion rules, Minecraft version, loader, and a screenshot or world location.

## What if the original modded block is missing?

If the saved original block is missing or unreadable, restoration falls back to dirt. Back up the world before removing mods.

## What is the performance cost?

It depends on entity counts and the number of tracked blocks. Decay runs once per second and skips unloaded chunks. Stored path records still use memory.

## Report a problem

Use [GitHub Issues](https://github.com/ahm3tak1f616/Safepaths/issues). Include your Safepaths, Minecraft, and loader versions; relevant mods; reproduction steps; and `latest.log` or a crash report when available.
