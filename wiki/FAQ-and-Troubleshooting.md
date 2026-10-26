# ❓ FAQ & Troubleshooting

Find answers to common questions and troubleshooting solutions for Safepaths below.

---

## 🙋 Frequently Asked Questions

### Q: What is Safepaths' server performance cost?
**A:** Cost depends on entity counts and the amount of tracked terrain. There is no zero-lag guarantee. The implementation limits work in several ways:
1. **Periodic Batch Processing**: Decay maintenance runs in `LevelTickEvent` once every **20 ticks** (1 second), rather than on every tick.
2. **Chunk Index**: Path records are grouped by chunk. Maintenance skips individual unloaded positions and does not force-load chunks. Stored unloaded records still consume memory.
3. **Weak Reference Caching**: In-memory position caches use `WeakHashMap`, allowing inactive entities to be garbage-collected immediately.
4. **Transient Modifiers**: Speed bonuses are transient attributes and are never written into entity NBT save files.

### Q: Does Safepaths need to be installed on both client and server?
**A: Yes, recommended.**
- The server manages step counting, block changes, and world saves (`safepaths_memory.dat`).
- Clients require the mod to view and use the in-game configuration screen (`CustomConversionsScreen`) and localized text messages.

### Q: Can I make paths permanent so they never decay?
**A:** Use a self-mapping rule for the road block, such as `minecraft:gravel -> minecraft:gravel` or `minecraft:stone_bricks -> minecraft:stone_bricks`. These are speed-only roads rather than trample conversions.

Increasing `decayTime` only delays decay. Its maximum, `240000`, is ten Minecraft days of inactivity at normal tick speed; it does not make natural paths permanent. There is currently no global decay-disable switch.

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
- If editing a dedicated server config, edit the server's file and restart the server to ensure changes apply. `/reload` reloads datapacks and is not a general TOML configuration reload command.

### Issue: "The in-game config button is disabled."
- In NeoForge, configuration screens are accessible from the title screen or pause menu via **Mods ➔ Safepaths ➔ Config**.
- While connected to a dedicated multiplayer server, the config screen shows server-owner instructions instead of a local editor. Ask the server owner to update the server's file.

### Issue: "A road directly above water developed a missing block."
- Older decay logic could restore sand or gravel that then fell through the water. The updated logic keeps the path in place until support makes restoration safe, and also guards against unsafe conversion targets.
- This prevents new holes from that cause; it does not reconstruct blocks already lost. If the original terrain was dirt or grass, report the conversion rules and affected location because falling terrain does not explain that case.

---

## 🐛 Bug Reports & Feature Requests

Found a bug or have an idea to make Safepaths even better?
- **Issue Tracker**: [GitHub Issues](https://github.com/ahm3tak1f616/Safepaths/issues)
- Please include:
  1. Minecraft version (`1.21.1`) and NeoForge version.
  2. Safepaths mod version (e.g., `2.0.1`).
  3. A list of any other relevant biome or terrain mods.
  4. Your `latest.log` or crash report if an error occurred.
