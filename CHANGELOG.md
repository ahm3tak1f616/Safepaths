# Changelog

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
