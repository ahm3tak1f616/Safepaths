# 🎨 In-Game Configuration & Conversions GUI

Safepaths includes a comprehensive, interactive in-game visual editor that allows you to configure block transformations and road speeds without ever touching a text editor or restarting the game.

---

## 🖥️ Accessing the Configuration Menu

1. Press `Esc` in-game and navigate to **Mods**.
2. Select **Safepaths** from the list of loaded mods.
3. Click **Config**. NeoForge uses its configuration screen; Forge 1.20.1 uses Safepaths' dedicated options screen.
4. Click **Configure Custom Conversions...** to launch the visual palette editor.

While connected to a dedicated server, the config screen instead explains that the server owner must edit `config/safepaths-common.toml` on the server. The editor does not provide remote server configuration.

---

## 🎛️ GUI Layout Overview

The GUI is divided into two primary working areas:

```
┌───────────────────────────────────────────────┬─────────────────────────────┐
│ Safepaths: Custom Conversions                 │ Speed Boost: [ON]  [ 0.20 ] │
├───────────────────────────────────────────────┼─────────────────────────────┤
│ CONVERSION TABLE (Left Pane)                  │ BLOCK PALETTE (Right Pane)  │
│                                               │ [ Search blocks...        ] │
│ [Grass] ──▶ [Dirt Path]       [Delete]        │ ┌───┬───┬───┬───┬───┬───┐   │
│ [Sand]  ──▶ [Dirt Path]       [Delete]        │ │🧱 │🌱 │🪨 │🪵 │🌾 │🧊 │   │
│ [Gravel]──▶ [Gravel] (Speed)  [Delete]        │ └───┴───┴───┴───┴───┴───┘   │
│                                               │ Live grid with item icons   │
│ [+ Add Conversion]                            │ and animated selection.     │
├───────────────────────────────────────────────┴─────────────────────────────┤
│ [ Done / Save ]                                           [ Cancel ]        │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1. Left Pane: Conversion Table (`ConversionTableWidget`)
- Displays all currently active transformation and roadway rules.
- Each row shows:
  - **Source Slot**: Block icon and registry ID of the ground block before trampling.
  - **Arrow Indicator (`->`)**: Visual flow of the conversion.
  - **Target Slot**: Block icon and registry ID of the resulting path or roadway.
  - **Delete Button (`-`)**: Instantly removes the conversion rule.
- Click **[+ Add Conversion]** at the bottom of the list to create a new conversion row.

### 2. Right Pane: Interactive Block Palette (`BlockPaletteWidget`)
- A searchable visual catalog of all registered blocks in the game, including blocks from other installed mods.
- **Search Bar**: Type any block name or mod namespace (e.g. `moss`, `biomesoplenty`, `path`) to filter the palette in real-time.
- **Technical Block Filtering**: Operator and technical blocks (command blocks, barriers, light blocks, jigsaw blocks, moving pistons) are automatically hidden to keep the palette clean and safe.
- **Visual Feedback**: Hovering over blocks displays animated highlights and tooltips with the localized item name and resource identifier.

---

## 🛠️ Step-by-Step: Adding a New Path or Speed Road

1. Click **[+ Add Conversion]** in the left table to add a new empty row.
2. Click the **Source** input box or slot icon on the new row (highlighted with an active border).
3. Search or click a block in the right-hand **Block Palette** (e.g. `minecraft:coarse_dirt`).
4. Click the **Target** input box on the same row.
5. Search or click the destination path block in the palette (e.g. `minecraft:dirt_path`).
   - *Tip for Speed Roads*: Select the same block for both source and target (e.g. `minecraft:gravel` ➔ `minecraft:gravel`) to create a speed-only roadway!
6. Click **[Done]** at the bottom of the screen to save your changes instantly to `safepaths-common.toml`.

---

## 🌍 International Locale & Decimal Support

Both editions support international decimal input for the master speed multiplier (introduced in 2.0.1):
- Accepts both standard period (`0.25`) and European comma (`0,25`) decimal separators.
- Seamlessly validates inputs across all international keyboards and OS locale settings without crashing or resetting to 0.
- Unsaved speed and search text survive resizing and returning from the defaults confirmation. Non-finite speed values such as `NaN` are ignored.

---

## ⚖️ Precedence & Conflict Handling

If multiple rules are entered for the same source block, Safepaths applies deterministic conflict resolution:
- **Explicit Transformations Override Self-Mapping**: An explicit change (such as `A -> B`) always takes precedence over a self-mapping rule (`A -> A`).
- **First Declared Priority**: When multiple distinct target transformations exist for the same source block, the first declared rule in the list takes precedence.

Only the winning targets grant road speed. Invalid or unsafe conversion targets do not become active road rules. Air variants, liquids, non-solid blocks, and block entities cannot be targets, and world placement still requires a surviving, supported result.
