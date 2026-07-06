# SafePaths (Minecolonies)

SafePaths is a lightweight, standalone NeoForge mod that brings organic path formation to your Minecraft world. Walking over the same blocks repeatedly will gradually trample them into dirt paths. Established paths reward entities with a configurable movement speed boost, making travel through your base or village faster and more immersive.

## Features
* **Organic Path Creation:** Traverse the same block multiple times to naturally form a path. 
* **Speed Boost:** Walking on established paths grants a seamless, transient movement speed bonus.
* **Smart Decay:** Unused paths will naturally revert to dirt over time, keeping the environment dynamic.
* **Ultimate Farmland Protection:** Vanilla farmland and modded crop blocks are strictly protected from being trampled into paths.
* **MineColonies Integration (Optional):** Citizens, visitors, and mercenaries contribute to path creation and benefit from the speed boost. Barbarians are explicitly blocked from exploiting your infrastructure.

## Configuration
The mod is highly customizable to fit any modpack's playstyle. You can adjust the following parameters in the config file:
* `requiredPasses`: Number of steps required to turn a block into a path.
* `speedMultiplier`: The intensity of the speed boost.
* `decayTime`: How long an unused path takes to revert to dirt.
* `constructionTime`: Memory reset timer for incomplete path formation.

## Installation
1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1.
2. Drop the `safepaths-[version].jar` file into your `.minecraft/mods` folder.

*Note: SafePaths operates purely through server-side logic. It works flawlessly in both singleplayer worlds and multiplayer servers.*

## License
This project is open-source and available under the [MIT License](LICENSE).
