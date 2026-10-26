# ⚡ Speed Boosts & Roadways

Walking on paths in Safepaths grants a realistic, satisfying movement speed increase. This section details how the speed modifier works, the transition smoothing algorithms, and how to create instant speed roads using **Self-Mapping**.

---

## 🏃 How the Path Speed Boost Works

When standing or walking on a recognized path, Safepaths applies a movement speed attribute modifier directly to the entity:

- **Modifier ID**: `safepaths:path_speed`
- **Attribute**: `Attributes.MOVEMENT_SPEED`
- **Operation**: `AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL`
- **Transient**: The modifier is not serialized into player NBT, so a saved player does not retain this boost after a restart. Normal path checks reapply it when appropriate.

### Configuration Controls
- **`enableSpeedBoost`** *(boolean, default: `true`)*: Master toggle for path speed boosts.
- **`speedMultiplier`** *(double, default: `0.2`)*: Fractional speed boost applied while on paths.
  - `0.2` = **+20% speed** (feels natural, similar to light running).
  - `0.5` = **+50% speed** (fast highway travel).
  - Valid range: `0.0` to `5.0`.

---

## 🧈 Smooth Movement & Anti-Jitter System

In standard Minecraft, walking across uneven terrain (such as stairs, slabs, step-ups, or 1-block gaps) causes speed modifiers to toggle on and off violently, causing nauseating FOV zoom flashing.

Safepaths solves this with two intelligent algorithms:

### 1. Bounding Box Footprint Tracking (`isEntityOnOrNearPath`)
Rather than querying a single integer coordinate at the player's center, Safepaths expands the entity's actual Axis-Aligned Bounding Box (AABB):
- Evaluates the entire footprint (`minX` to `maxX`, `minZ` to `maxZ`).
- Checks vertical depth beneath the entity (`minY - 1.0` to `minY - 0.05`).
- Walking along the edge of a path block or across block seams maintains continuous speed without stutter.

### 2. The 15-Tick Grace Window
When you step off a path block, your speed boost does **not** instantly cut off:
- Safepaths tracks the timestamp of the last path interaction (`LAST_PATH_TICKS`).
- The speed boost remains active for **15 game ticks** (~0.75 seconds) after leaving a path.
- This creates smooth sprinting across:
  - Stone or wood stairs bridging path elevation changes.
  - Slabs and stepping stones.
  - 1-block gaps, puddles, or bridges.

---

## 🛑 Safety & Movement Exclusions

To prevent exploits and unnatural behavior, the speed boost is automatically deactivated if any of the following conditions are met:
- **Swimming**: Entity is in water or bubble columns.
- **Lava**: Entity is immersed in lava.
- **Elytra Flying**: Entity is actively fall-flying.
- **Creative / Spectator Flight**: Player is flying in creative mode or in spectator mode.
- **Long Falls**: Entity fall distance exceeds `3.5` blocks (prevents absurd horizontal launch speeds when jumping off cliffs onto paths).

---

## 🛣️ Speed-Only Roadways (Self-Mapping)

Introduced in Safepaths 2.0, **Self-Mapping** is a game-changing feature for builders and town planners.

### The Concept
What if you want built roads—like cobblestone streets, gravel highways, or stone brick plazas—to grant the path speed boost, without ever being trampled into dirt paths or decaying back into raw soil?

By mapping an eligible road block to **itself** in the conversion list:
```toml
"minecraft:gravel -> minecraft:gravel"
"minecraft:stone_bricks -> minecraft:stone_bricks"
"minecraft:smooth_stone -> minecraft:smooth_stone"
```

### Key Advantages of Self-Mapped Roads:
1. ⚡ **Instant Speed Boost**: Entities immediately receive the full speed boost when walking over them.
2. 🛡️ **Zero Trample Alteration**: The block will **never** convert into a dirt path or undergo transformation.
3. 🧹 **Zero Memory Overhead**: Self-mapped blocks do not store trample counters or decay timestamps, keeping world save files clean and fast.
4. 🧱 **Built-in Default**: As of version 2.0.0, `minecraft:gravel -> minecraft:gravel` is enabled by default so gravel roads function as authentic speed highways!

If a newly self-mapped road already has a decay record from an older conversion, maintenance discards that record while leaving the road intact. Self-mapping does not make air, liquids, non-solid blocks, or block entities eligible conversion targets.

### How to Add Speed Roads
You can define self-mapped roads in two ways:
1. **Via `safepaths-common.toml`**: Add `"namespace:block_name -> namespace:block_name"` to `customConversions`.
2. **Via the In-Game GUI**: Open the **Conversions GUI**, add a row, and select the same block for both the source and target slots!
