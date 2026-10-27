# 📜 Changelog

All notable changes to Safepaths are documented here.

---

## [Unreleased]

---

## [2.0.2] — 2026-10-28

### 🛡️ Fixed

* Fixed holes appearing when unused paths restored sand, red sand, gravel, or other falling terrain above air, liquids, or replaceable blocks, including roads directly over water. Paths now remain intact until support returns, then restore their original terrain automatically.
* Rejected air variants, liquids, non-solid blocks, and block entities as conversion targets. Unsupported falling targets and targets that cannot survive at the position no longer replace terrain; blocks with inventories or other block-entity data are protected from trampling.
* Invalidated stale decay records when roads are replaced, and cleared tracking for every block affected by piston chains and player placement.
* Preserved original block properties and creator permission context across world saves while retaining compatibility with older path-memory data.
* Enforced MineColonies break/place permissions for players and colony ownership for civilian path creators. Decay rechecks creator permissions before changing claimed ground.
* Corrected duplicate conversions so the first explicit rule wins and unused targets no longer grant road speed. Explicit conversions continue to override self-mapping rules.
* Applying a self-mapping rule to an existing tracked road now clears its old decay record without changing the road.
* Preserved unsaved speed and search inputs when the conversion editor reopens or resizes. Multiplayer clients now receive server configuration instructions instead of editing unrelated local settings.
* Preserved pending Forge config-screen inputs across resizing and conversion-editor navigation, and rejected non-finite speed input in both editors.
* Isolated Forge regression-test settings from live configuration-file reloads to prevent intermittent CI failures without weakening road-safety assertions.
* Prevented jumping or creative flight from counting as additional footsteps on the same ground block.

### Changed

* Indexed path records by chunk so decay maintenance skips individual unloaded positions without force-loading terrain. Active path timestamps are refreshed at most once per second, and disabled speed boosts skip footprint scanning.
* Build and publishing workflows now require the in-world GameTests to pass both with and without MineColonies before completing.
* Publishing now pins the release tag, loader, Minecraft version, Java version, and optional MineColonies dependency, and uploads the same tested JAR to GitHub, Modrinth, and CurseForge.
* Backported the road-safety, permissions, persistence, GUI, and maintenance fixes to Minecraft 1.20.1 on Forge with Java 17. The security scan now targets the Forge branch and Java version.

### Upgrade notes

* Existing configurations and older path-memory saves remain compatible. Back up your world before updating, then replace the mod JAR and restart.
* The road-safety fix prevents unsupported terrain restoration from creating new holes; it does not rebuild blocks that were already missing.

---

## [2.0.1] — 2026-10-07

### 🛡️ Fixed & Improved
* 🥾 **Sub-Block & Path Collision Targeting** — Switched position tracking to entity supporting block coordinates (`getOnPos()`), fixing an issue where walking on dirt paths or mud targeted the block underneath, preventing decay timer refreshes and causing ghost sub-surface paths.
* ⏳ **Chunk Unload Decay Preservation** — Paths in unloaded chunks are now preserved in world memory until reloaded rather than being prematurely purged, ensuring abandoned pathways properly decay and revert to nature when players return.
* 🔀 **Conversion Rule Precedence** — Explicit block transformations (`A -> B`) now cleanly supersede self-mapping speed rules (`A -> A`) if both are declared for the same block.
* 🛡️ **Speed-Only Roadway Guard** — Guarded path state determination so self-mapped roadways are permanently immune to converting into dirt paths through any code path.
* 🦺 **World Save Null Safety** — Added fallback null-safety in `PathMemorySavedData` during NBT serialization.
* 🌐 **Localized Decimal Input Support** — Configuration GUI now handles both dot (`.`) and comma (`,`) decimal separators gracefully for all keyboard layouts and international locales.
* 🎯 **GUI Slot & Input Synchronization** — Clicking directly into custom conversion text boxes immediately synchronizes slot selection with the block palette.
* 🧪 **Automated Self-Mapping GameTest** — Added in-engine GameTest verifying self-mapped block recognition and path immunity.

---

## [2.0.0] — 2026-09-30

### 🚀 Added
* ⚡ **Speed-Only Road Blocks (Self-Mapping)** — Map any block to itself (e.g. `stone_bricks -> stone_bricks` or `gravel -> gravel`) to grant full movement speed bonus without any trampling, block alteration, or decay tracking.
* 🛡️ **Conflict & Duplicate Resolution** — Deterministic resolution for duplicate and conflicting conversion rules using `putIfAbsent`. First declared rule safely takes priority.
* 🌐 **Full Biome Mod Integration** — Native support for vanilla-block worldgen overhauls (*Terralith*, *Geophilic*) alongside pre-configured defaults for *Oh The Biomes We've Gone*, *Biomes O' Plenty*, and *Regions Unexplored*.

### 🔄 Changed
* 🪨 **Gravel Road Defaults** — Changed default gravel behavior from converting into dirt path to pure speed road (`minecraft:gravel -> minecraft:gravel`), keeping natural gravel pathways intact.

### 🛡️ Improved & Fixed
* ⚙️ **Config GUI Null Safety** — Fixed tooltip null safety in `SafePathsClient` to ensure the NeoForge configuration menu opens reliably without errors.
* 🧹 **Clean Memory Management** — Pruned redundant trample checks and tracking overhead on protected and speed-only blocks.

---

## [1.3.0] — 2026-09-30

### 🚀 Added
* 🎨 **Visual Conversion GUI** — Configure custom block paths in-game with an interactive block palette and live search.
* 🌍 **Out-of-the-Box Mod Support** — Added defaults for vanilla soils (`grass_block`, `dirt`, `sand`, `gravel`), *Oh The Biomes We've Gone*, *Biomes O' Plenty*, and *Regions Unexplored*.
* ⏳ **Smart Decay Restoration** — Decayed paths automatically revert to their authentic original ground block.
* 🏷️ **Custom Path Tag** — Added `#safepaths:is_path` to register custom road blocks that grant speed bonuses and refresh decay.

---

## [1.2.0] — 2026-09-29

### 🚀 Added
* 🌐 **Multi-Language Support** — Native translations for 9 languages (`en_us`, `de_de`, `es_es`, `fr_fr`, `ja_jp`, `ru_ru`, `zh_cn`, `tr_tr`, `pt_br`).

### 🛡️ Improved
* 🏃 **Speed Boost Smoothing** — 15-tick grace period over stairs, slabs, and 1-block gaps prevents camera and FOV jitter.
* 👞 **Hitbox Detection** — Bounding box footprint tracking eliminates edge stutter near block borders.
* 🔄 **Decay Timer Maintenance** — Walking on existing paths consistently refreshes decay timers.
* ⚡ **Transient Attributes** — Dynamic movement speed modifiers are transient to prevent world save NBT clutter.

---

## [1.1.0] — 2026-07-17

### 🔄 Changed
* 📜 **License & Permissions** — Switched to All Rights Reserved for modpack permissions and distribution clarity.
* ⚡ **Code Optimization** — Refined entity stepping checks and memory layout for version bump.

---

## [1.0.2] — 2026-07-14

### 🚀 Added
* 🧪 **Automated GameTests** — In-engine suite verifying path tags, farmland immunity, and path memory.
* ⚙️ **In-Game Config Screen** — NeoForge configuration menu for timing, footstep thresholds, and toggles.
* 🏛️ **MineColonies Compatibility** — Tagged metadata and protection for colony citizens and structures.

### 🛡️ Improved & Fixed
* 💾 **Persistent Dimension Memory** — Trample progress and decay state saved reliably across restarts via `SavedData`.
* 🛡️ **Farmland Protection** — Farmland and crop blocks are strictly immune to being stamped into paths.
* 🧹 **Memory Leak Cleanup** — Stale and distant unloaded path entries safely pruned.

---

## [1.0.1] — 2026-07-07

### 🛡️ Improved
* ⏱️ **LevelTick Decay Processing** — Moved cleanup from entity ticks to level ticks to prevent accidental chunk loading.

---

## [1.0.0] — 2026-07-06

* 🎉 Initial release for Minecraft 1.21.1 on NeoForge.
