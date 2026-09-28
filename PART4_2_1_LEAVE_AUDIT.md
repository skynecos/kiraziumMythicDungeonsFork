# PART 4.2.1 — Manual leave audit

Scope:
- /leave
- FunctionLeaveDungeon
- PlayerLeaveDungeonEvent cancellation
- safe exit fallback

## Bugs found

1. InstancePlayable.removePlayer() called AbstractInstance.removePlayer(), but if
   PlayerLeaveDungeonEvent was cancelled the subclass continued its cleanup.
   This could remove the player from livingPlayers, restore inventory/exp and
   mutate dungeon state even though leaving had been prevented.

   Fix: after the superclass call, if the player still exists in the instance
   player list, return immediately.

2. /leave's stale-world fallback could receive a null Player.getRespawnLocation().
   The existing Part42Compat.safeExitFallback() now provides:
   respawn location -> primary world spawn -> current player location.

## Paper 26.1.2 runtime probe

Cancellation test assertions:
- players list preserved: PASS
- livingPlayers preserved: PASS
- MythicPlayer.instance preserved: PASS
- safeExitFallback non-null: PASS
- NoSuchMethodError: none
- NoSuchFieldError: none
- ClassNotFoundException: none

Marker:
KIRAZIUM_PART4_2_1_CANCEL_RESULT=PASS
