# Minecraft 1.7.10 development

This project targets Forge **10.13.4.1614** for Minecraft **1.7.10**, using
Java 8, Gradle 4.10.3, and anatawa12's ForgeGradle 1.2 fork.
Migration reference: https://github.com/anatawa12/ForgeGradle-example

## Windows commands

Run these in PowerShell from this project directory:

```powershell
.\dev.bat setupDecompWorkspace
.\dev.bat build
.\dev.bat runClient
```

`dev.bat` selects the installed Temurin Java 8 JDK for this invocation and
stores Gradle downloads in `.gradle-user-home`. It does not change your global
Java settings. To use a different Java 8 installation, set `JAVA8_HOME` first:

```powershell
$env:JAVA8_HOME = 'C:\path\to\jdk8'
```

The initial setup needs internet access and downloads/decompiles Minecraft.
Build output goes into `build/libs/` (`dMod-1.0.jar`).
`runClient` launches the development game with the example mod loaded.

Verified locally: `setupDecompWorkspace idea eclipse build` completed
successfully with Java 8 and Gradle 4.10.3. Interactive gameplay has not been
tested.

## IDE setup

For VS Code, workspace settings select Java 8 for Gradle and the project,
and share the download cache used by `dev.bat`. After changing these settings,
run **Developer: Reload Window** from the command palette. The Java language
server itself can continue using its bundled Java 21 runtime.

For IntelliJ IDEA, run `.\dev.bat idea`, then open the generated project.
Select Java 8 as the project SDK. If importing through Gradle, also select
Java 8 as the Gradle JVM, use the wrapper, and set the Gradle user home to
this project's `.gradle-user-home` directory.

For Eclipse, run `.\dev.bat eclipse`, then import the project as an existing
Java project. Configure its JRE as the Java 8 JDK.

## Starter mod

The mod is **dMod**, authored by **Darahz**, with registry ID `dmod`.
Its entry point is `src/main/java/com/darahz/dmod/DMod.java`.

- `blocks/BlockExample.java`: a placeable block registered as `dmod:example_block`.
- `blocks/tile/TileEntityExample.java`: stores a separate click counter per placed
  block, saves it to NBT, and calls `markDirty()` when the value changes.
- `items/ItemExample.java`: an item registered as `dmod:example_item` that
  displays a message when right-clicked in the air.
- `src/main/resources/assets/dmod/lang/en_US.lang`: names and chat messages.

Textures are wired to transparent 16x16 PNG placeholders under
`src/main/resources/assets/dmod/textures/`:

- `items/example_item.png`
- `items/void_key.png`
- `blocks/example_block.png` (used on every face)
- `items/zenite_ingot.png`
- `items/void_fragment.png`
- `blocks/zenite_ore.png`
- `blocks/void_condenser.png`

Paint these files to add your artwork. Until painted, they render transparent.

### Try the examples

1. Run `.\dev.bat runClient` and open a Creative world.
2. Find both examples in the **dMod** Creative tab.
3. Place two Example Blocks and right-click them: each has its own counter.
4. Save and quit, reopen the world, and click again to check persistence.
5. Right-click in the air with the Example Item to see its message.

The counter belongs to the placed block; breaking and replacing the block resets
it. Counter changes happen on the server, and chat delivers the result to the
player. There is no client-side counter display or custom synchronization yet.

## Void dimension

The **dMod Void** generates only air, with no terrain or structures. Entering
with `/dvoid` places a single bedrock landing block at `0, 64, 0`.
Player-built blocks still save normally. Its sky and fog are black,
with no sun, moon, stars, clouds, rain, snow, or lightning. Skylight remains
enabled, so exposed blocks receive daytime lighting.

The dimension reports a fixed daylight time of **6000 ticks (noon)**. Game ticks
and scheduled block updates continue; the Overworld's daylight and weather are
not changed. Natural creature spawning is disabled in the void.

### Void Key

Find the **Void Key** in the dMod Creative tab, or craft one:

```text
  O
O E O
  D
```

`O` = obsidian (3), `E` = Eye of Ender (1), `D` = diamond (1).

Hold right-click for **2 seconds** to charge. Portal particles spiral around you
and notes rise in pitch, followed by a teleport sound. Release early to cancel.
The key uses `textures/items/void_key.png` with an enchantment glint, is never consumed, and has a
**5-second cooldown per player**, shared across multiple keys.

Enter from the Overworld while standing on the ground in Survival (Creative
players may also enter while flying). You arrive on the bedrock block. Charge
the key again to return to your saved position, including after reconnecting.
Return travel is refused if the destination is obstructed or, for non-flying
players, its floor is missing. Dismount before travelling.

### Testing command

With cheats enabled (or operator permission on a server), `/dvoid` is an instant
shortcut using the same travel logic:

1. Stand in the Overworld and run `/dvoid`.
2. You arrive standing at `0.5, 65, 0.5` on the bedrock block, with flight off.
3. Build outward from the bedrock block. Keep the two blocks above it clear for entry.
4. Run `/dvoid` again to return to the saved Overworld position.

The return position survives saving and reconnecting. The command is testing
access; the Void Key works in Survival without cheats or operator permissions.
If you die in the void, normal respawning returns you to the Overworld.

The default dimension/provider ID is **72**. Configure `dimensions.voidDimensionId`
in `config/dmod.cfg` (under `eclipse/config` for the development client) if another
mod uses that ID. Keep IDs consistent on client and server and do not change the
ID after using the dimension in a saved world.

Code lives in `src/main/java/com/darahz/dmod/dimension/`:
`WorldProviderVoid` controls the environment, `ChunkProviderVoid` generates air,
`VoidDimension` registers the ID, and `VoidTravel` handles shared travel.
`CommandVoid` provides testing access; `items/ItemVoidKey.java` controls charging.

In-game checks: confirm the black sky with clouds enabled; use `/time query daytime`
twice to confirm noon stays fixed; confirm Overworld weather does not appear in
the void; place blocks and revisit after saving; test `/dvoid` on a dedicated
server as well as in single-player. For the key, test early release, successful
charging, cooldown with two keys, blocked destinations, and saving/reconnecting
before returning. Effects and travel need in-game verification.

## Zenite and the Void Condenser

Progression: **Zenite Ore -> furnace -> Zenite Ingot -> Void Condenser -> Void Fragments**.

Zenite Ore generates in new Overworld chunks only, replacing stone. The initial
balance is six vein attempts per chunk, generator size six, with origins from
Y=8 through Y=40. Actual veins depend on the surrounding stone and may extend
slightly beyond their origin height. Existing chunks are not retroactively
modified. Mine with an iron pickaxe or better and smelt each ore into one ingot
(0.7 furnace XP).

All crafting and smelting recipes, including the Void Key, now live in
`src/main/java/com/darahz/dmod/init/ModRecipes.java`.

Condenser recipe:

```text
Z G Z
Z E Z
Z O Z
```

`Z` = Zenite Ingot (6), `G` = glass (1), `E` = Eye of Ender (1), `O` = obsidian (1).

Place the condenser in the dMod Void. While its chunk is loaded, it produces one
Void Fragment per 200 server ticks (10 seconds at normal tick rate), without fuel.
It stores up to 64 and pauses when full. Right-click to collect as much as fits in
your inventory; uncollected fragments stay in the machine. Right-click an empty
condenser to see its status. Outside the void, it does not produce anything.

Output and partial progress save with the tile entity. Breaking the machine drops
its stored fragments separately; partial progress resets when it is replaced.
Use a stone pickaxe or better to recover the condenser block. This initial version
has no GUI, hopper support, or offline production. Void Fragments have no further
crafting uses yet.

The four new textures are blank 16x16 PNGs ready for painting, following the
existing texture setup. The ore and condenser will be invisible until painted.

In-game checks still needed: ore generation in fresh terrain, pickaxe requirements,
smelting/crafting, production in the void versus Overworld, full/partially full
inventories, the 64-fragment cap, save/reload, and breaking a filled condenser.
