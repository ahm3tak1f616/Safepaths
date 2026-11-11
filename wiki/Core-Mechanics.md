# Path creation and decay

## Footsteps

Players and entities in `#safepaths:path_creators` can form paths by walking over eligible ground. A new step is counted when the entity moves onto a different supporting block while on the ground. Standing still, jumping in place, flying, and spectator movement do not add passes.

By default, 20 passes must occur within 24,000 game ticks: one Minecraft day at normal tick speed. The window starts with the first counted step. Later steps do not extend it. If the window expires, the next counted step starts again at one.

Set `enablePathCreation = false` to stop new construction passes. Existing roads still receive authorized traffic. With `sneakPreventsPathCreation = true`, sneaking players preserve new ground by adding no construction passes; they still refresh existing roads.

## Conversions

When enough passes have been counted, Safepaths uses the configured conversion for that block. Without an explicit rule, eligible mud and moss become dirt paths; other eligible blocks use Minecraft's shovel conversion table, then the fallback for IDs containing `grass`, `dirt`, `soil`, `sand`, or `gravel`.

A conversion from a block to itself makes it a permanent speed road. It does not change the block or create a decay record. An explicit conversion to a different block overrides a self-mapping rule; among explicit conversions, the first rule for a source wins.

Air, liquids, non-solid blocks, and block entities are not valid conversion targets. If a target cannot survive or would fall without support, the ground stays unchanged.

## Decay

Safepaths records the original block state when it creates a path. Authorized traffic refreshes that path's activity timer, at most once per second. After `decayTime` ticks without that traffic, the path returns to its original terrain. The default is 72,000 ticks, or three Minecraft days. Restoration is checked once per second.

Set `enablePathDecay = false` to disable restoration. Records and elapsed time remain, so expired paths can restore when decay is enabled again. Traffic can still refresh tracked roads while decay is disabled. Creation and decay switches do not disable the road speed bonus.

Placed or shovel-made paths have no automatic decay record. A different replacement block clears the old record. Changing an existing tracked road to a speed-only road also clears its record without changing the block.

If sand, gravel, or another original block cannot stay at the position, the path remains until restoration is safe. Missing or unreadable original blocks fall back to dirt.

Tracked roads also retain their original ground for [per-conversion speed](Speed-Boosts-and-Roadways). Use the [inspect command](Inspect-Command) to check a block's current progress and status.

## Protection and saving

Farmland, block entities, blocks from MineColonies, and blocks in `#safepaths:cannot_become_path` cannot be trampled.

On MineColonies claims, players need break and place permissions. Civilian path creators must belong to that colony. Decay checks the saved creator's permissions or colony ownership before changing claimed ground.

Path records are saved with the world. Decay skips unloaded chunks and resumes when they load; Safepaths does not load chunks to process paths. Breaking, placing, explosions, and piston movement clear affected records.
