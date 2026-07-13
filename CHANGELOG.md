# Changelog

## Unreleased

### Fixed
- Path trample and decay now track dimension + position, so different worlds no longer share the same path data

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

Initial release for Minecraft 1.21.1 (NeoForge).
