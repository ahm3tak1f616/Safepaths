# Changelog

## Unreleased

- Clearer configuration labels, conversion-editor help, and installation instructions.
- One release process for the Forge 1.20.1 and NeoForge 1.21.1 downloads.

## 2.0.2 — 2026-10-28

- Prevented sand and gravel from falling away when a path restores unsupported terrain, including roads over water. Existing holes are not rebuilt.
- Rejected unsafe conversion targets and protected blocks with inventories or other block-entity data.
- Cleared stale path records after block replacements and piston movement.
- Preserved original block properties and creator permissions when saving paths.
- Applied MineColonies permissions to path creation and decay.
- Fixed conflicting conversion rules and removed decay records when a road becomes speed-only.
- Preserved unsaved speed and search inputs when resizing the editor or returning from a confirmation.
- Showed server configuration instructions to multiplayer clients instead of editing local settings.
- Stopped jumping in place and creative flight from adding footsteps on the same block.

Existing configurations and older path saves remain compatible. Back up your world before updating.

## 2.0.1 — 2026-10-07

- Fixed ground detection on dirt paths and mud.
- Preserved path records in unloaded chunks until they can be processed again.
- Made explicit conversions take priority over speed-only road rules.
- Fixed speed-only blocks being converted through fallback rules.
- Added support for comma decimal input in the conversion editor.
- Fixed palette selection when clicking conversion fields.
- Improved handling of incomplete path save data.

## 2.0.0 — 2026-09-30

- Added permanent speed roads by mapping a block to itself.
- Made gravel a speed-only road by default.
- Added conversion defaults for selected blocks from Oh The Biomes We've Gone, Biomes O' Plenty, and Regions Unexplored.
- Fixed duplicate conversion handling and cleaned up unused path records.
- Fixed a configuration-screen tooltip error.

## 1.3.0 — 2026-09-30

- Added the visual block-conversion editor.
- Added default terrain conversion rules and the `safepaths:is_path` tag.
- Restored the saved original ground when unused paths decay.

## 1.2.0 — 2026-09-29

- Added translations for nine languages.
- Reduced speed changes at path edges and small gaps.
- Improved path activity refreshes and road detection beneath entities.
- Stopped storing the temporary road speed bonus in entity saves.

## 1.1.0 — 2026-07-17

- Updated the license and modpack permissions.
- Improved footstep tracking and memory use.

## 1.0.2 — 2026-07-14

- Added the configuration menu and MineColonies support.
- Saved path progress and decay state across restarts.
- Added farmland protection and improved path-memory cleanup.

## 1.0.1 — 2026-07-07

- Moved decay processing to level ticks to avoid loading chunks for path updates.

## 1.0.0 — 2026-07-06

- Initial release for Minecraft 1.21.1 NeoForge.
