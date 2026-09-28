# PART 4 — Player / Queue / Teleport / Leave / Death / Respawn

Target:
- Paper 26.1.2 build 74
- Java 25
- NBT-API 2.16.1

## Queue / Ready Check

Two real issues were found.

### 1. Async Player API access
The original ready-check path used asynchronous Bukkit tasks while calling Player APIs
(location, sound, titles/messages and player lookup). The Paper 26.1.2 port moves
player-facing ready-check work to the main server thread.

Runtime probe deliberately started the ready flow from an async task.

Result:
- ready check started: PASS
- ready/not-ready marshalled to main thread: PASS
- player API calls off main thread: 0
- marker: KIRAZIUM_PART4_QUEUE_RESULT=PASS

### 2. readyPlayers type mismatch
QueueData stores UUID values in readyPlayers, but the old isPlayerReady path compared
against a MythicPlayer object. The port compares the player's UUID.

## Teleport

Verified on Paper 26.1.2:
- same-world teleport: PASS
- same-world teleport with passenger retention: PASS
- Entity.teleportAsync path: PASS
- RETAIN_PASSENGERS linkage: PASS

All active MythicDungeons core forceTeleport call sites in the player/instance flow use
Player targets. A non-player ArmorStand cross-world passenger experiment was not treated
as a core failure because that is not the plugin's active player dungeon path.

## Leave / Death / Respawn / disconnect

Paper 26.1.2 member/link audit for this area:
- missing classes: 0
- missing methods: 0
- missing fields: 0

Runtime probe:
- PlayerDeathEvent -> MythicPlayer dead=true: PASS
- PlayerRespawnEvent -> dead=false: PASS
- dungeon respawn location applied: PASS
- PlayerQuitEvent normal path clears disconnecting state: PASS
- reconnect recovery clears stale savedPosition: PASS
- reconnect recovery clears StoredExitLocation: PASS
- marker: KIRAZIUM_PART4_2_RESULT=PASS

### 3. Stale exit state after offline timeout cleanup
If a player was removed from an instance while offline, savedPosition and
StoredExitLocation could remain. On later joins this could cause repeat recovery
teleports, including after restart.

The Paper 26.1.2 port clears both values after reconnect recovery has been processed.

### 4. Offline cleanup was not persisted after the normal quit save
KickOfflinePlayers can remove a player after the player has already disconnected.
The removal path can change inventory/game-state after Paper's normal quit-time player
save has occurred.

The port calls Player.saveData() at the end of InstancePlayable.removePlayer only when
the Player is offline.

Direct helper verification:
- offline player -> saveData called: PASS
- online player -> saveData not called: PASS
- marker: KIRAZIUM_PART4_2_OFFLINE_PERSIST_RESULT=PASS

Bytecode verification confirms InstancePlayable.removePlayer ends by invoking
Part42Compat.persistIfOffline(Player).

### 5. /leave recovery null respawn
LeaveCommand's recovery branch passed Player.getRespawnLocation() directly to
forceTeleport when no savedPosition existed. Paper may return null when the player has
no personal bed/anchor respawn.

The port now resolves:
1. personal respawn when present;
2. primary-world spawn when personal respawn is absent;
3. current location only as an emergency no-world fallback.

The complete PART 4.2 runtime probe passed again after this patch.

## PART 4 result

Queue/ready, active player teleport, leave, death, respawn, quit and reconnect cleanup
are verified for the tested Paper 26.1.2 paths. The fixes above must be merged together
with PART 1 and PART 3 changes in the final combined build.
