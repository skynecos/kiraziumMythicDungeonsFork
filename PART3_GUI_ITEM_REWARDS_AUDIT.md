# PART 3 — GUI / Item / Skull / Inventory / Rewards

Target:
- Paper 26.1.2 build 74
- Java 25
- NBT-API 2.16.1

## Real incompatibility found

The legacy `utility.helpers.SkullUtil` used:
- `com.mojang.authlib.GameProfile`
- `GameProfile.getProperties()`
- reflection into the private SkullMeta `profile` field.

On Paper 26.1.2 this produced:

`NoSuchMethodError: GameProfile.getProperties()`

The helper was replaced by Paper's supported `Bukkit.createProfile(...)`,
`ProfileProperty`, and `SkullMeta.setPlayerProfile(...)` API.

Runtime re-test:
- legacy helper after patch: PASS
- existing avngui PlayerProfile helper: PASS

## GUI / Item / Inventory runtime probe

PASS:
- ItemUtils function tool creation + verification
- ItemUtils room tool creation + verification
- dungeon key PersistentDataContainer marker
- blocked menu item + display name
- both next/previous custom skull paths
- Window / Button / GUIInventory creation and sorting
- LootTableItem randomization + serialize/deserialize
- LootTable randomization + menu initialization + serialize/deserialize

The plugin's normal onEnable also executed all no-argument GUI initializers:
- function menu
- trigger menu
- conditions menus
- gate trigger menus
- item-select trigger/function menus
- reward menu
- revival menu
- multi-function menus
- connector whitelist
- recruitment browser

## Reward-function runtime probe

With a valid instanceWorld attached:
- FunctionReward: PASS
- FunctionRandomReward: PASS
- FunctionLootTableRewards: PASS

Their reward GUI, cooldown menu and hotbar initialization all completed without
NoSuchMethodError / NoSuchFieldError / ClassNotFoundException.

## Static legacy scan after patch

Within the PART 3 scope:
- Authlib GameProfile refs: 0
- CraftMetaSkull/private CraftBukkit inventory refs: 0
- legacy MaterialData/setDurability/CraftInventory refs: 0

## Remaining limitation

A real connected Player is still required to exercise the final click/open/claim
path of /rewards and player inventory delivery. The underlying classes and API
links were loaded and the reward GUI/function initialization paths passed.
