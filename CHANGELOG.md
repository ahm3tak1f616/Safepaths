# Changelog

## 1.2.0 - 2026-09-29

### Added
- **Multi-Language Support**: Added native translations for German (`de_de`), Spanish (`es_es`), French (`fr_fr`), Japanese (`ja_jp`), Russian (`ru_ru`), Simplified Chinese (`zh_cn`), Turkish (`tr_tr`), and Brazilian Portuguese (`pt_br`).

### Improved
- **Speed Boost Continuity & Smoothing**: Added a smooth 15-tick grace period allowing seamless traversal over stairs, slabs, 1-block non-road gaps, corners, and jumps without sudden speed drop-offs or jarring FOV/POV stutter.
- **Footprint Hitbox Detection**: Stepping and path detection now checks the entity's full bounding box rather than a single center point, eliminating edge jitter when walking near block borders.
- **Stepping Logic & Map Efficiency**: Optimized step handling over non-path blocks by verifying trample state prior to map deletions, reducing unnecessary map mutations.
- **Entity Lifecycle Management**: Active entities that are removed or dead are immediately pruned from cache tracking.

### Fixed
- **Dirt Path Decay Refresh**: Fixed an issue where walking on an existing dirt path skipped refreshing its decay timer due to an early return, ensuring frequently walked paths stay maintained.
- **Transient Speed Modifier**: Reverted modifier registration to transient to prevent client prediction desyncs and avoid writing temporary movement attributes into player save NBT.
- **In-Game Config GUI Localization**: Added all required NeoForge configuration category title, button, and tooltip localization keys.
- **FOV Flickering on Jumps**: Sprint-jumping across dirt paths no longer causes the FOV to repeatedly zoom in and out.
- **ConcurrentModificationException on Path Decay**: Decoupled block restoration updates from path memory iteration during decay cycles.
- **Terrain Air Void Bug**: Added a safe fallback to dirt when original block state data is corrupted or missing, preventing decayed paths from creating holes in the world.

### Development
- **GameTest Modernization**: Modernized GameTest mock player creation API and added tests verifying dirt path decay timer refreshing upon walking.
- **Codebase & Inspection Polish**: Addressed IDE code inspection warnings, cleaned unused parameters and modifiers, streamlined GameTest setup helpers, and aligned documentation formatting.

## 1.1.0 - 2026-07-17

### Changed
- License switched from MIT to **All Rights Reserved**: modpacks and redistribution require prior permission (contact via GitHub)
- Optimization and version bump

## 1.0.2 - 2026-07-14

### Added
- GameTests for pathable tags, path memory, path formation, and farmland protection (`./gradlew runGameTestServer`)
- In-game config screen (Mods → Safepaths → Config) wired to lang keys
- Optional MineColonies dependency metadata in `neoforge.mods.toml`
- README datapack tag documentation
- MIT `LICENSE` and aligned license metadata (`gradle.properties` / mods.toml)
- Optimized mod logo (`icon.png`, 128×128)

### Improved
- Entity tick path logic is gated on an allow-list before block lookups (cheaper for unrelated mobs)
- Speed boost re-applies when `speedMultiplier` / `enableSpeedBoost` change mid-session
- Cleanup interval scales with `constructionTime` / `decayTime` (clamped 20–1000 ticks)
- Unloaded path entries older than `2 × decayTime` are pruned so memory cannot grow forever
- Last-step cache cleared on level unload and server stop
- Datapack tags: `#safepaths:path_creators`, `#safepaths:path_blocked`, `#safepaths:cannot_become_path` (same defaults as before)

### Fixed
- Path decay timers now refresh when entities walk on existing dirt paths
- Path/trample memory is saved per dimension (`SavedData`), so decay and original-block restore survive restarts and stay isolated between worlds
- Stale path memory is cleared on explosions, piston moves, and fluid block placement, and invalid entries are dropped when the block no longer matches
- Speed boost now uses `ADD_MULTIPLIED_TOTAL` with default `0.2` so it matches Speed I (+20%) for all entities
- NeoForge/Minecraft dependency ranges now come from Gradle properties
- README accurately describes decay restore, farmland protection, and the speed boost
- Path trample and decay now track dimension + position, so different worlds no longer share the same path data
- Paths only form from steps while on the ground (flying, falling, and mid-jump no longer count)
- `requiredPasses` is limited to 1–1000 so invalid config values cannot break the mod

## 1.0.1 - 2026-07-07

### Changed
- Moved path cleanup and decay to `LevelTickEvent` instead of running it from entity ticks
- Only touch path/decay memory when the chunk is loaded (`isLoaded`), so unloaded areas are not forced in

### Fixed
- Memory leak / forced chunk loading from cleaning paths during entity ticks

### Other
- Version bump to 1.0.1
- Gradle / mod metadata cleanup (`gradle.properties`, `neoforge.mods.toml`)

## 1.0.0 - 2026-07-06

- Initial release for Minecraft 1.21.1 (NeoForge).
