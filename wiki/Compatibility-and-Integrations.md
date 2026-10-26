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
- **Claim Permissions**: Players need both break and place permissions to form paths on claimed ground. Civilian creators may form paths only in their own colony; other tagged mobs cannot modify a claim.
- **Decay Permissions**: Saved player permissions or civilian colony ownership are rechecked before decay. Newly claimed land and revoked permissions can therefore pause restoration. Legacy claim records without creator context wait for authorized traffic.
- **Block Entities**: Inventories, machines, and other blocks with block-entity data cannot be trampled.
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

Mods that generate landscapes using the supported vanilla ground blocks need no additional conversion rules:
- **Terralith** (Starmute)
- **Geophilic**
- **Tectonic**
- **Continents**

All grass types, sands, gravel slopes, and mud flats in these worldgens naturally support pathing and decay.

---

## 🪓 Shovel Pathing Heuristic (Third-Party Mods)

For any mod not explicitly covered in the default list:
1. Safepaths queries vanilla Minecraft's `ShovelItem.getShovelPathingState(state)`, which reads the shovel flattening table. A modded block implementing only tool-action hooks is not automatically discovered by this lookup; add an explicit conversion for it.
2. For eligible pathable blocks without an explicit or shovel-table conversion, registry identifiers containing `grass`, `dirt`, `soil`, `sand`, or `gravel` fall back to `minecraft:dirt_path`. Safety and claim checks still apply.
