# ❓ FAQ & Troubleshooting

Find answers to common questions and troubleshooting solutions for Safepaths below.

---

## 🙋 Frequently Asked Questions

### Q: Does Safepaths cause server tick lag (low TPS)?
**A: No.** Safepaths is designed from the ground up for minimal performance impact:
1. **Periodic Batch Processing**: Decay maintenance runs in `LevelTickEvent` once every **20 ticks** (1 second), rather than on every tick.
2. **Zero Forced Chunk Loading**: Safepaths checks `serverLevel.isLoaded(pos)` before inspecting any path. Unloaded chunks are skipped entirely until a player approaches them.
3. **Weak Reference Caching**: In-memory position caches use `WeakHashMap`, allowing inactive entities to be garbage-collected immediately.
4. **Transient Modifiers**: Speed bonuses are transient attributes and are never written into entity NBT save files.

### Q: Does Safepaths need to be installed on both client and server?
**A: Yes, recommended.**
- The server manages step counting, block changes, and world saves (`safepaths_memory.dat`).
- Clients require the mod to view and use the in-game configuration screen (`CustomConversionsScreen`) and localized text messages.

### Q: Can I make paths permanent so they never decay?
**A: Yes.** You have two options:
1. **Extend Decay Time**: In `config/safepaths-common.toml`, set `decayTime = 240000` (10 full Minecraft days of zero foot traffic).
2. **Use Speed-Only Roadways (Self-Mapping)**: Map your road block to itself (e.g. `minecraft:gravel -> minecraft:gravel` or `minecraft:stone_bricks -> minecraft:stone_bricks`). Self-mapped blocks never decay and never undergo trample transformation.

### Q: What happens if I remove a biome mod whose blocks were turned into paths?
**A: Safepaths handles this safely.** When a path decays, Safepaths checks if the original saved block still exists in the registry. If the block no longer exists (e.g. the mod was removed), Safepaths automatically substitutes standard `minecraft:dirt`, preventing corruptions, crashes, or missing tile entities.

### Q: Why didn't my grass block turn into a path after stepping on it?
Check the following:
1. **Different Block Positions**: Stepping in place or jumping on the same block does not count as multiple steps. You must walk between different blocks.
2. **Time Window**: Were the steps taken within `constructionTime` (default: 24,000 ticks = 20 minutes)? If you waited too long between passes, the counter resets.
3. **Protected Blocks**: Is the block farmland or tagged in `#safepaths:cannot_become_path`?
4. **Spectator Mode**: Players in spectator mode cannot create paths.
5. **Entity Whitelist**: Non-player mobs must be tagged in `#safepaths:path_creators` (e.g. MineColonies citizens) to create paths.

---

## 🛠️ Troubleshooting

### Issue: "I changed `safepaths-common.toml` but the game didn't update."
- If playing singleplayer, save and exit your world before editing the file, or use the **In-Game Conversions GUI** (under **Mods ➔ Safepaths ➔ Config**), which updates config values live.
- If editing a dedicated server config, restart the server or run `/reload` to apply changes.

### Issue: "The in-game config button is disabled."
- In NeoForge, configuration screens are accessible from the title screen or pause menu via **Mods ➔ Safepaths ➔ Config**.
- If opening on a multiplayer server, the config file is server-authoritative; client GUI changes apply to local settings.

---

## 🐛 Bug Reports & Feature Requests

Found a bug or have an idea to make Safepaths even better?
- **Issue Tracker**: [GitHub Issues](https://github.com/ahm3tak1f616/Safepaths/issues)
- Please include:
  1. Minecraft version (`1.21.1`) and NeoForge version.
  2. Safepaths mod version (e.g., `2.0.1`).
  3. A list of any other relevant biome or terrain mods.
  4. Your `latest.log` or crash report if an error occurred.
