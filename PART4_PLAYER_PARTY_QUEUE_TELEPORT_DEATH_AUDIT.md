# PART 4 — Player / Party / Queue / Teleport / Leave / Death

Target runtime:
- Paper 26.1.2 build 74
- Java 25
- NBT-API 2.16.1

## Real issues found and fixed

### 1. Queue / ready-check thread safety
The legacy queue flow executed Player API calls from asynchronous tasks, including:
- Player.getLocation()
- Player.playSound()
- ready-check messages/titles
- Bukkit.getPlayer()

The Paper 26.1.2 compatibility implementation keeps all Player/Bukkit interaction on the primary thread.

A separate logic bug was also fixed:
`readyPlayers` stores UUID values, but the old `isPlayerReady()` path compared against a MythicPlayer object.

Runtime audit:
- ready-check entered from an async caller
- Player API calls observed: 19
- off-main Player API calls: 0
- ready UUID detection: PASS
- notReady cancellation: PASS

Result:
`KIRAZIUM_PART4_QUEUE_RESULT=PASS`

### 2. Recruitment listing thread safety
Two legacy paths were unsafe:
- recruitment broadcaster used `runTaskTimerAsynchronously` while iterating online players and sending UI/messages
- password handling occurred directly from `AsyncChatEvent` and could call party mutation / sound / Player APIs from the async chat thread

Fix:
- broadcaster now runs on the primary-thread timer
- AsyncChatEvent captures/cancels input only; party mutation and Player API delivery are marshaled to the primary thread

Runtime correct-password test:
- party.addPlayer called: true
- password prompt removed: true
- Player API calls observed: 11
- off-main Player API calls: 0

Result:
`KIRAZIUM_PART4_RECRUIT_RESULT=PASS`

### 3. Party chat delivery
The custom `AsyncMythicPartyChatEvent` remains asynchronous as designed.
The legacy implementation, however, also performed Player.displayName() and Player.sendMessage() on that async worker.

Fix:
- event firing remains asynchronous
- after event listeners finish, Player-facing delivery is marshaled to the primary thread

Runtime test:
- message delivered: true
- Player API calls observed: 8
- off-main Player API calls: 0

Result:
`KIRAZIUM_PART4_PARTYCHAT_RESULT=PASS`

## Teleport audit

Same-world Paper async teleport and MythicDungeons `Util.forceTeleport()` were tested with and without passengers:
- Paper teleportAsync, no flag: PASS
- Paper teleportAsync + RETAIN_PASSENGERS: PASS
- Util.forceTeleport without passenger: PASS
- Util.forceTeleport with passenger: PASS

A non-player ArmorStand cross-world test returned false. Direct Paper
`Entity.teleportAsync()` and direct synchronous `Entity.teleport()` returned false in the same scenario as well.
Therefore this is not treated as a MythicDungeons-specific compatibility regression.

The normal dungeon teleport callsites use Player entities. A real connected Player is required for a fully authentic client-backed cross-world teleport test.

## Leave / Quit / Death / Respawn

Audited:
- LeaveCommand
- InstanceListener
- PlayListener
- MythicPartyListener
- AbstractInstance / InstancePlayable player removal paths

Paper 26.1.2 API verification includes:
- PlayerDeathEvent deathMessage(Component), drops and cancellation APIs
- PlayerRespawnEvent setRespawnLocation
- Player getRespawnLocation
- Player$Spigot.respawn()
- PlayerQuitEvent / PlayerChangedWorldEvent player access
- sync BukkitScheduler.runTaskLater paths

Death/respawn/quit delayed operations use synchronous scheduler paths.

## Descriptor-level member audit

The PART 4 class set was parsed at classfile constant-pool level.
Every external `org.bukkit`, `io.papermc` and `com.destroystokyo` method/field reference was resolved against the Paper 26.1.2 classpath.

Result:
`PART4_MEMBER_AUDIT ok=241 missing=0`

## PART 4 conclusion

Confirmed Paper 26.1.2 fixes required:
1. queue ready-check thread safety + ready UUID logic
2. recruitment broadcaster/password thread safety
3. party-chat Player delivery thread safety

No additional removed Paper/Bukkit API member was found in Leave / Quit / Death / Respawn paths.
