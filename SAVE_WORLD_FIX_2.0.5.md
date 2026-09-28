# MythicDungeons Paper 26.1.2 save/world fix — v2.0.5

## User-visible bug
Editing a dungeon and saving appeared to succeed, but starting the dungeon opened an empty world.

## Root causes found on Paper 26.1.2

1. **World.save() no longer guarantees the asynchronous chunk writer is flushed.**
   The four edit/procedural save callsites are patched to `World.save(true)`.

2. **Paper 26.1.2 stores custom dimensions under the primary world's dimension directory.**
   MythicDungeons 2.0.1 copied instances to the legacy server-root `/<instance>` path.
   The port now derives the actual Paper dimension parent from the loaded primary world's
   `getWorldFolder().getParentFile()`.

3. **Paper now stores a world UUID in `data/paper/metadata.dat`.**
   Copying this file causes duplicate-world protection to reject the cloned instance.
   Instance creation now removes `uid.dat`, `session.lock`, and
   `data/paper/metadata.dat` from the copied instance before `WorldCreator` loads it.

4. **Legacy `GameRule.SPAWN_CHUNK_RADIUS` no longer exists on Paper 26.1.2.**
   Both remaining usages were replaced by `World.setKeepSpawnInMemory(false)`.

## Exact integration test

Environment:
- Temurin Java 25
- Paper 26.1.2 build 74
- NBT-API 2.16.1
- MythicDungeons 2.0.5-KIRAZIUM-26.1.2-SAVE-WORLD-FIX

Test sequence used MythicDungeons' own API:
1. create/load a CLASSIC dungeon template;
2. `createEditSession()`;
3. change STONE -> DIAMOND_BLOCK inside the edit instance;
4. call `InstanceEditable.saveWorld()`;
5. call `createPlaySession()`;
6. load the playable dungeon instance;
7. verify the same block is DIAMOND_BLOCK.

Result:

`KIRAZIUM_MYTHICDUNGEONS_EXACT_SAVE_TEST PASS`

Observed Paper paths:
- edit instance: `world/dimensions/minecraft/md_worldfix_itest0`
- playable instance: `world/dimensions/minecraft/md_worldfix_itest1`

Process exited cleanly with exit code 0.
