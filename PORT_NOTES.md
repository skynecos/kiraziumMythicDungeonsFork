# Paper 26.1.2 port notes

Target: **Paper 26.1.2 build 74 / Java 25**

## Changes

1. Replaced the legacy exact-version gate with a parser that recognizes Paper's
   `26.1.2.build.*` era Bukkit version string and adds `v26_1_2`.
2. Replaced MythicDungeons' embedded legacy NBT reflection path used by dungeon
   block-entity schematics with a bridge to **NBT-API 2.16.1**.
3. NBT-API is therefore a required plugin dependency for this port.
4. Disabled the old ReflectionUtils memory-leak NMS hack. Its legacy
   EntityPlayer/pathfinder field reflection is not valid on 26.1.2 and is not
   required for dungeon gameplay.
5. Kept the plugin name `MythicDungeons` so existing data/config folders and
   integrations continue to resolve the same plugin identity.

## Runtime verification

Verified locally against:
- Java 25.0.4.1 Temurin
- Paper 26.1.2 build 74
- NBT-API 2.16.1

Observed startup:
- NBT-API: `NMS support 'MC26_1' loaded!`
- NBT-API: `Success! This version of NBT-API is compatible with your server.`
- MythicDungeons: `Mythic Dungeons v2.0.1 initialized! Happy dungeon-ing!`

End-to-end schematic NBT test:
- placed a real chest,
- inserted 7 diamonds,
- captured it using `StructurePieceBlock.from()`,
- placed it at another location using `StructurePieceBlock.placeAt()`,
- verified the destination chest contained exactly 7 diamonds.

Result: **PASS**.

The network-related Mojang/Paper update-check errors seen in the isolated local
test environment are unrelated to the plugin and were caused by that runtime
having no external DNS/network access.
