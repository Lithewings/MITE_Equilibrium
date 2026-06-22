# [MITE_Equilibrium]

A hardcore survival mod built on Minecraft Fabric 1.21.1, inspired by MITE-R196.

[![Modrinth](https://img.shields.io/badge/Modrinth-00AF5C?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/mod/miteequilibrium)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## About

This mod uses Minecraft Fabric 1.21.1 as its framework, recreating most of the classic MITE-R196 settings while adding other details to improve the gameplay experience.

The mod offers a linear progression gameplay experience, with the goal of accessing the End. There are no excessive, frivolous details; there is more than one path to reach the objective. What you must do is carefully consider your current situation and make the right choices.

Standard playthrough length: **72 to 128 in-game days**.

## Features

### Core Difficulty Mechanics
- Crafting takes time, and advanced recipes require even more.
- Multiple crafting bench tiers with level requirements.
- Greatly reduced mining speed.
- **Bloodmoon Blight**: Crops turn pale and wither during a Bloodmoon.
- Advanced animal AI with faster movement and smarter pathfinding.
- **Malnutrition**: Lack of fruits and vegetables causes health regen reduction and constant hunger drain.
- Villages only generate far away, after crafting a metal tool and enough time has passed.

### Player Attributes
- Start with 6 health and 6 hunger; maximum increases with experience levels.
- No health regen from food. Regenerate 1 health every 960 ticks.
- Reduced block breaking and attack reach (can be extended by certain items or sneaking).

### Furnace Tiers
- **Clay Furnace**: Cannot smelt ores; lit by flammable items.
- **Cobblestone Furnace**: Smelts most ores; lit by coal.
- **Obsidian Furnace**: Higher temperature, smelts Mithril; lit by lava buckets.
- **Netherrack Furnace**: Smelts Adamantium; lit only by Blaze Rods.

### Anvil Durability
- 64 durability points, consumed per use.
- Three visual stages: intact, chipped, damaged. Destroyed at 0.
- Repair with Iron Blocks (24 points each). Pristine anvils cannot be repaired directly, but two can be combined.

### Death & Respawn
- Entering the world costs 1 health and inflicts short debuffs.
- First death: keep everything, respawn at spawn.
- Level >5 and ≤35: lose all XP above level 1, keep items.
- Level >35: lose 5 levels' worth of XP, keep items.
- Each death increases the Ender Dragon's base armor by 1.

### Moon Phases
- **Day 1**: Blue Moon (no monsters).
- **Full Moon & Bloodmoon**: Cannot sleep.
- **Every 32 days**: Bloodmoon (thunderstorm day, extreme monster buffs).
- **Yellow Moon** (Full Moon before Bloodmoon): Faster crop growth.
- **Every 128 days**: Blue Moon (faster ticks, animals spawn nearby).

### New Ores, Crops & Tools
- Copper, Silver, Mithril, **Adamantium**. Silver weapons grant Regeneration on undead killing blows.
- Wooden, Stone, and Diamond tools removed (Wooden Shovel kept). Durability heavily reduced.
- Onion crop added (found in chests). All crop growth slowed and requires full sky light.
- Right-click compass to see coordinates.

### Progression & World Changes
- Early game: hunt and gather. Farming is extremely slow.
- Obtain Flint and metal nuggets from Gravel to craft your first benches.
- Villages generate without villagers; collect crops to start your own settlement.
- Strongholds with End Portals only beyond 12,000 blocks from spawn. Eyes of Ender only locate that specific stronghold.

### Dimensions & Portals
- Overworld portals teleport you to world spawn or your last bed.
- Portal above bedrock at **Y=-60** leads to the **Underworld**, a perilous cave dimension containing Diamond, Mithril, and Adamantium veins.
- Bedrock is unbreakable in survival. Illegal breaking kills nearby players.
- Nether access requires a portal built over lava at Y=-60 in the Underworld.

### New Mobs
- **Ghoul**: Slowness and mouse sensitivity debuff.
- **Longdead** (Underworld): Spawns with armor; melee and ranged variants.
- **Invisible Stalker**: Fully invisible, nearly silent, inflicts Blindness. Destroys torches.
- **Revenant** (rare zombie variant below Y=0): Full iron gear, high damage.
- **Fire Elemental**: Immune to non-enchanted weapons; vulnerable to snowballs; burns players.
- Zombies see through blocks, break blocks, attack animals, and eat meat.
- Endermen attack players holding Ender Pearls. After >30 deaths, they become permanently hostile.

### New Enchantments
- **Reach** (Bucket): Extended block interaction distance.
- **Speed** (Leather Boots): Movement speed bonus.
- **Extend** (Longsword): Increased attack range.

### Advanced Difficulty Rules (Optional)
- **Think Twice Before Action**: Lose half health on login (minimum 3).
- **Rainy Season**: Drastically increased rain duration.
- **Extinction**: Passive animals don't spawn; start with 16 leather.
- **Blight**: Crops only grow with bone meal (except pumpkins/melons).
- **Desolation**: Removes villages and outposts.
- **Universal Aggro**: Zombified Piglins and Endermen are always hostile.
- Activating any advanced rule locks difficulty to Hard and disables Creative/Spectator.



## License

This project is licensed under the [MIT License](LICENSE). *(Choose your license and update the link)*



 
