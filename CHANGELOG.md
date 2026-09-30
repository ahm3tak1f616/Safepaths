# 📜 Changelog

All notable changes to Safepaths are documented here.

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
* 👢 **Hitbox Detection** — Bounding box footprint tracking eliminates edge stutter near block borders.
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
