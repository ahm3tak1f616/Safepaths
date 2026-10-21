# 🚀 Getting Started with Safepaths

This guide covers everything you need to install, configure, and play with **Safepaths** in singleplayer or on multiplayer dedicated servers.

---

## 📋 Requirements

| Requirement | Supported Version | Notes |
| :--- | :--- | :--- |
| **Minecraft** | `1.21.1` | Target game release |
| **Mod Loader** | **NeoForge** `21.1.235+` | Required loader (FML major version 1+) |
| **Java** | `Java 21` (64-bit) | Standard for Minecraft 1.20.5+ |
| **Optional Mods** | [MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies)<br>[Biomes O' Plenty](https://www.curseforge.com/minecraft/mc-mods/biomes-o-plenty)<br>[Oh The Biomes We've Gone](https://www.curseforge.com/minecraft/mc-mods/oh-the-biomes-weve-gone)<br>[Regions Unexplored](https://www.curseforge.com/minecraft/mc-mods/regions-unexplored) | Enhanced features and default conversions |

---

## 💾 Installation Guide

### Singleplayer / Client
1. Download and install [NeoForge 21.1.235+](https://neoforged.net/) for Minecraft `1.21.1`.
2. Download the latest `safepaths-x.x.x.jar` from:
   - [Modrinth Releases](https://modrinth.com/mod/safepaths)
   - [CurseForge Releases](https://www.curseforge.com/minecraft/mc-mods/safepaths)
   - [GitHub Releases](https://github.com/ahm3tak1f616/Safepaths/releases)
3. Open your `.minecraft` directory:
   - **Windows**: Press `Win + R`, type `%appdata%\.minecraft`, and press Enter.
   - **macOS**: `~/Library/Application Support/minecraft`
   - **Linux**: `~/.minecraft`
4. Place the `.jar` file into the `mods` folder.
5. Launch the game using your NeoForge profile.

### Dedicated Multiplayer Server
Safepaths operates on both server and client:
- **Server-Side**: Handles block stepping counters, path conversion logic, world save persistence (`SavedData`), and decay reversion.
- **Client-Side**: Provides the in-game configuration GUI (`CustomConversionsScreen`) and localized text messages.

To install on a dedicated server:
1. Ensure your server is running **NeoForge 21.1.235+** for Minecraft 1.21.1.
2. Drop `safepaths-x.x.x.jar` into your server's `mods/` directory.
3. Start the server. The configuration file will automatically generate at `config/safepaths-common.toml`.

> [!NOTE]
> For the best experience on multiplayer servers, install Safepaths on **both** the server and connected clients so players can access the in-game visual configuration menu and receive full client-side synchronization.

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
