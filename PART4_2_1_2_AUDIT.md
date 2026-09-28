# PART 4.2.1 / 4.2.2 audit

Target: Paper 26.1.2 build 74 / Java 25

## 4.2.1 Leave cancellation

Found a real logic bug in `InstancePlayable.removePlayer(MythicPlayer, boolean)`:
`AbstractInstance.removePlayer()` correctly fires `PlayerLeaveDungeonEvent` and aborts when cancelled, but the subclass continued with living-player removal, inventory/XP restore and cleanup.

Patch behavior: after the superclass call, if the player is still present in the instance player list, return immediately.

Runtime marker:
`KIRAZIUM_PART4_2_1_CANCEL_RESULT=PASS`

## 4.2.2 Death / Respawn

Focused runtime probe verified:
- death marks MythicPlayer dead
- respawn clears dead flag
- configured dungeon respawn location is applied
- no Paper 26.1.2 linkage errors in death/respawn path

Runtime marker:
`KIRAZIUM_PART4_2_RESULT=PASS`

Member audit for PART 4 scope: 241 resolved, 0 missing.
