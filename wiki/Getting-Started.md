# 🚀 Getting Started with Safepaths

This guide covers everything you need to install, configure, and play with **Safepaths** in singleplayer or on multiplayer dedicated servers.

---

## 📋 Requirements

| Minecraft | Required loader | Java | Safepaths 2.0.2 file |
| :--- | :--- | :--- | :--- |
| `1.21.1` | **NeoForge** `21.1.235+` | Java 21 (64-bit) | `safepaths-2.0.2-1.21.1-neoforge.jar` |
| `1.20.1` | **Forge** `47.3.0+` | Java 17 (64-bit) | `safepaths-2.0.2-1.20.1-forge.jar` |

[MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies), [Biomes O' Plenty](https://www.curseforge.com/minecraft/mc-mods/biomes-o-plenty), [Oh The Biomes We've Gone](https://www.curseforge.com/minecraft/mc-mods/oh-the-biomes-weve-gone), and [Regions Unexplored](https://www.curseforge.com/minecraft/mc-mods/regions-unexplored) are optional. Use companion mod versions matching your Minecraft version and loader.

---

## 💾 Installation Guide

### Singleplayer / Client
1. Install [NeoForge 21.1.235+](https://neoforged.net/) for Minecraft `1.21.1`, or [Forge 47.3.0+](https://files.minecraftforge.net/) for Minecraft `1.20.1`.
2. Download the matching Safepaths JAR listed above from:
   - [Modrinth Releases](https://modrinth.com/mod/safepaths)
   - [CurseForge Releases](https://www.curseforge.com/minecraft/mc-mods/safepaths)
   - [GitHub Releases](https://github.com/ahm3tak1f616/Safepaths/releases)
3. Open your `.minecraft` directory:
   - **Windows**: Press `Win + R`, type `%appdata%\.minecraft`, and press Enter.
   - **macOS**: `~/Library/Application Support/minecraft`
   - **Linux**: `~/.minecraft`
4. Place the matching `.jar` file into the `mods` folder. Remove the old Safepaths JAR when updating; do not keep both versions or loader editions together.
5. Launch the game using the matching NeoForge or Forge profile.

### Dedicated Multiplayer Server
Safepaths operates on both server and client:
- **Server-Side**: Handles block stepping counters, path conversion logic, world save persistence (`SavedData`), and decay reversion.
- **Client-Side**: Provides the in-game configuration GUI (`CustomConversionsScreen`) and localized text messages.

To install on a dedicated server:
1. Ensure your server uses one of the Minecraft/loader/Java combinations in the requirements table.
2. Drop the matching Safepaths JAR into your server's `mods/` directory, replacing any older Safepaths JAR.
3. Start the server. The configuration file will automatically generate at `config/safepaths-common.toml`.

> [!NOTE]
> The server owns path creation, decay, and speed settings. The client install provides the local configuration GUI; it does not edit or synchronize a remote server's common configuration. The server owner manages the server's file. Back up your world before updating. Existing configurations and older path-memory saves remain compatible.

---

## 🎮 First Steps In-Game

1. **Walk Along Natural Paths**: Pick a route you walk frequently (e.g. from your base to a mine or animal pen).
2. **Watch the Ground Transform**: After 20 passes (default setting), grass and soil beneath your feet will compress into a dirt path block!
3. **Feel the Speed Boost**: Step on your new dirt path to experience a smooth +20% movement speed increase.
4. **Build Custom Roadways**: Place down gravel or custom road blocks to take advantage of instant speed boosts.
5. **Open the Config Screen**: Press `Esc` ➔ **Mods** ➔ **Safepaths** ➔ **Config** to tune step counts, decay times, or add custom block conversions.

---

## ⏩ What's Next?
- Learn how pathing steps, decay timers, and chunk persistence work in **[Core Mechanics](Core-Mechanics)**.
- Discover how to build instant speed roadways without block changes in **[Speed Boosts & Roadways](Speed-Boosts-and-Roadways)**.
- Customize block transformations visually using the **[In-Game GUI & Palette](In-Game-Configuration-and-GUI)**.
