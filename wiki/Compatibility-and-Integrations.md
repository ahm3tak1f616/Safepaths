# 🌐 Compatibility & Integrations

Safepaths is engineered to integrate seamlessly into diverse modded environments, from vanilla+ overhauls to massive colony-building modpacks.

---

## 🏛️ MineColonies Integration

Safepaths was designed with special attention to **[MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies)**, bringing a living, breathing pulse to bustling towns.

### 1. Citizen Desire Paths
In MineColonies, citizens perform daily routines: lumberjacks commute to forests, miners travel to shafts, deliverymen navigate warehouses, and guards walk continuous patrol perimeters.
- As citizens repeat these commutes day after day, authentic dirt paths organically emerge connecting colony buildings!
- Friendly NPCs recognized:
  - `minecolonies:citizen`
  - `minecolonies:visitor`
  - `minecolonies:mercenary`

### 2. Colony Protection System
Colonies are heavily protected from griefing or unwanted alterations:
- **Farmland Immunity**: Farmland plots inside and outside colonies cannot be turned into paths.
- **Colony Block Immunity**: Any block belonging to the `minecolonies` namespace (including town halls, builder huts, barracks, and colony structures) is strictly protected.
- **Barbarian Blacklist**: Raider factions (`minecolonies:barbarian`, `minecolonies:archerbarbarian`, `minecolonies:chiefbarbarian`) are strictly barred from creating paths.

### 3. Worker Productivity
Colony citizens on dirt paths receive the movement speed boost, allowing them to traverse towns faster and deliver resources more efficiently!

---

## 🌲 Biome Mod Support

Safepaths includes pre-configured conversion defaults for major Minecraft biome mods:

### 1. Oh The Biomes We've Gone (BWG)
| Source Block | Target Path |
| :--- | :--- |
| `biomeswevegone:lush_grass_block` | `biomeswevegone:lush_dirt_path` |
| `biomeswevegone:lush_dirt` | `biomeswevegone:lush_dirt_path` |
| `biomeswevegone:sandy_dirt` | `biomeswevegone:sandy_dirt_path` |

### 2. Biomes O' Plenty (BOP)
| Source Block | Target Path |
| :--- | :--- |
| `biomesoplenty:origin_grass_block` | `minecraft:dirt_path` |

### 3. Regions Unexplored (RU)
| Source Block | Target Path |
| :--- | :--- |
| `regions_unexplored:peat_grass_block` | `regions_unexplored:peat_dirt_path` |
| `regions_unexplored:peat_dirt` | `regions_unexplored:peat_dirt_path` |
| `regions_unexplored:silt_grass_block` | `regions_unexplored:silt_dirt_path` |
| `regions_unexplored:silt_dirt` | `regions_unexplored:silt_dirt_path` |
| `regions_unexplored:chalk_grass_block` | `regions_unexplored:chalk_dirt_path` |
| `regions_unexplored:chalk_dirt` | `regions_unexplored:chalk_dirt_path` |

---

## 🗺️ Vanilla-Block Terrain Overhauls

Mods that generate custom landscapes using standard Minecraft blocks are **100% compatible out of the box** with no configuration required:
- **Terralith** (Starmute)
- **Geophilic**
- **Tectonic**
- **Continents**

All grass types, sands, gravel slopes, and mud flats in these worldgens naturally support pathing and decay.

---

## 🪓 Shovel Pathing Heuristic (Third-Party Mods)

For any mod not explicitly covered in the default list:
1. Safepaths queries vanilla Minecraft's `ShovelItem.getShovelPathingState(state)`. If a modded soil defines a path state for shovels, Safepaths adopts that exact path block automatically!
2. If shovel logic is absent, Safepaths analyzes registry identifiers for keywords (`grass`, `dirt`, `soil`, `sand`, `gravel`) and safely converts them to `minecraft:dirt_path`.
