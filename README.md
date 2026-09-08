# dMod

A Minecraft **1.7.10** Forge mod.

Built with [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) 1.4.9 instead of
the original ForgeGradle 1.2, because the legacy toolchain no longer resolves its dependencies
reliably and is pinned to JDK 8.

## Requirements

- A **JDK 17 or 21** on your `PATH` (this runs Gradle itself).
- Nothing else. Gradle downloads the JDK 8 toolchain used to compile the mod, plus Minecraft,
  Forge and the MCP mappings, on the first build.

## Getting started

```bash
./gradlew runClient     # first run decompiles Minecraft -- expect 5-15 minutes
./gradlew runServer     # dedicated server in ./run
./gradlew build         # produces the reobfuscated jar in build/libs
```

There is no `setupDecompWorkspace` step -- RFG wires the decompiled Minecraft in as a normal
dependency, so importing `build.gradle` into IntelliJ IDEA is all the IDE setup needed.

## Layout

| Path | Purpose |
| --- | --- |
| `src/main/java/com/darahz/dmod/DMod.java` | Mod entry point, `@Mod` annotation, lifecycle events |
| `src/main/java/com/darahz/dmod/Reference.java` | Mod id, name, version, proxy class paths |
| `src/main/java/com/darahz/dmod/proxy/` | Sided proxies -- client-only code stays in `ClientProxy` |
| `src/main/resources/mcmod.info` | Mod metadata shown in the in-game mod list |
| `src/main/resources/assets/dmod/lang/en_US.lang` | Localisation |

## Content

Backported from `.old/`, which was a **Minecraft 1.15.2 / Forge 31** mod -- so this
was a rewrite against the 1.7.10 API, not a copy. Registry events became
`GameRegistry` calls, block states became metadata, `ITextComponent` tooltips became
plain strings, recipe JSON became `GameRegistry.addRecipe`, and LWJGL 3 keyboard
polling became LWJGL 2.

### Items

| Item | Behaviour |
| --- | --- |
| Mob re-locator | Right click a creature to store it in NBT; right click a block to release it, or a spawner to set its creature |
| Spawner re-locator | Picks up a vanilla spawner and places it elsewhere, keeping its settings |
| Spawner re-programmer 2k^2 | Holds spawner settings; shift+scroll selects, shift+click adjusts, right click stamps them onto a spawner |
| Tear of disenchantment | Drop beside an enchanted item on the ground to pull its enchantments into books |
| Necklace of repair | Banks one charge per second carried, spends charges repairing damaged gear |
| Item attractor | Drags loose items to you while enabled |
| Crafting table linker | Binds to a crafting table, then opens it from anywhere for 1 XP |
| Mob repellant | Makes a mob permanently flee players; 16 uses |

Toggleable items (necklace, attractor) use **sneak + right click**.

### Blocks

| Block | Behaviour |
| --- | --- |
| Freezing Element | Lays snow on top, drips snowballs into a container above, freezes nearby water to ice and lava to obsidian |
| Mob Slaughter House | Kills mobs in range on a timer; right click with redstone/gold/iron/diamond to upgrade speed/targets/range |
| Reprogrammed Spawner | Spawns its stored creature on a timer; set the creature with the Mob re-locator |

### Recipes

Only two existed in `.old` and both are carried over: the Freezing Element and the
Spawner re-locator. The other six items have no recipe yet.

## Notes

- Mod id is `dmod`; the asset folder name (`assets/dmod/`) must always match it.
- `Reference.VERSION` and `version` in `build.gradle` are separate -- bump both together.
- Adding another mod to the dev environment: put its maven in `repositories` and the artifact in
  `dependencies` as `runtimeOnlyNonPublishable`.
- `linkSystemOpenAl` runs before `runClient`. LWJGL 2.9.4 ships no OpenAL native, so without
  it the game crashes in vanilla's `MusicTicker` with `UnsatisfiedLinkError` after about 45
  seconds. It links your system `libopenal.so.1` into `run/natives/lwjgl2`.
- The re-programmer sends its adjustments to the server over a `SimpleNetworkWrapper`
  channel; the server owns the values. Editing item NBT client-side alone does not persist.
