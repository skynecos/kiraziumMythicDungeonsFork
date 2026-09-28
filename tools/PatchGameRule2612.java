import javassist.*;

public final class PatchGameRule2612 {
  public static void main(String[] args) throws Exception {
    if (args.length != 3) throw new IllegalArgumentException("usage: <input-jar> <paper-api-jar> <out-dir>");
    ClassPool pool = new ClassPool(true);
    pool.insertClassPath(args[0]);
    pool.insertClassPath(args[1]);
    CtClass cc = pool.get("net.playavalon.mythicdungeons.api.parents.instances.AbstractInstance");
    cc.defrost();
    CtMethod m = cc.getDeclaredMethod("applyWorldRules");
    m.setBody("{" +
      "this.instanceWorld.setKeepSpawnInMemory(false);" +
      "this.instanceWorld.setAutoSave(false);" +
      "if (!this.config.getBoolean(\"Rules.SpawnMobs\", false)) {" +
      "  org.bukkit.entity.SpawnCategory[] cats = org.bukkit.entity.SpawnCategory.values();" +
      "  for (int i = 0; i < cats.length; i++) {" +
      "    if (cats[i] != org.bukkit.entity.SpawnCategory.MISC) this.instanceWorld.setTicksPerSpawns(cats[i], 0);" +
      "  }" +
      "} else if (!this.config.getBoolean(\"Rules.SpawnAnimals\", false)) {" +
      "  this.instanceWorld.setTicksPerSpawns(org.bukkit.entity.SpawnCategory.ANIMAL, 0);" +
      "  this.instanceWorld.setTicksPerSpawns(org.bukkit.entity.SpawnCategory.AMBIENT, 0);" +
      "  this.instanceWorld.setTicksPerSpawns(org.bukkit.entity.SpawnCategory.AXOLOTL, 0);" +
      "  this.instanceWorld.setTicksPerSpawns(org.bukkit.entity.SpawnCategory.WATER_AMBIENT, 0);" +
      "  this.instanceWorld.setTicksPerSpawns(org.bukkit.entity.SpawnCategory.WATER_ANIMAL, 0);" +
      "  this.instanceWorld.setTicksPerSpawns(org.bukkit.entity.SpawnCategory.WATER_UNDERGROUND_CREATURE, 0);" +
      "} else if (!this.config.getBoolean(\"Rules.SpawnMonsters\", false)) {" +
      "  this.instanceWorld.setTicksPerSpawns(org.bukkit.entity.SpawnCategory.MONSTER, 0);" +
      "}" +
      "if (this.config.getBoolean(\"Rules.DisableRandomTick\", true)) {" +
      "  this.instanceWorld.setGameRule(org.bukkit.GameRule.RANDOM_TICK_SPEED, java.lang.Integer.valueOf(0));" +
      "}" +
      "this.onApplyWorldRules();" +
    "}");
    cc.writeFile(args[2]);
    cc.detach();
  }
}
