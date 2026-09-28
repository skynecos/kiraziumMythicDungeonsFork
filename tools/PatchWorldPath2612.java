import javassist.*;

public final class PatchWorldPath2612 {
  public static void main(String[] args) throws Exception {
    if (args.length != 3) throw new IllegalArgumentException("usage: <input-jar> <paper-api-jar> <out-dir>");
    String input = args[0], paper = args[1], out = args[2];
    ClassPool pool = new ClassPool(true);
    pool.insertClassPath(input);
    pool.insertClassPath(paper);

    CtClass cc = pool.get("net.playavalon.mythicdungeons.api.parents.instances.AbstractInstance");
    cc.defrost();

    CtMethod unique = cc.getDeclaredMethod("getUniqueWorldName");
    unique.setBody("{" +
      "this.instName = this.getDungeon().getWorldName() + this.id;" +
      "java.io.File base = org.bukkit.Bukkit.getWorldContainer();" +
      "java.util.List worlds = org.bukkit.Bukkit.getWorlds();" +
      "if (worlds != null && !worlds.isEmpty()) {" +
      "  org.bukkit.World root = (org.bukkit.World) worlds.get(0);" +
      "  java.io.File parent = root.getWorldFolder().getParentFile();" +
      "  if (parent != null) base = parent;" +
      "}" +
      "java.io.File worldFolder = new java.io.File(base, this.instName);" +
      "if (!worldFolder.exists()) return worldFolder;" +
      "this.id++;" +
      "return this.getUniqueWorldName();" +
    "}");

    CtMethod copy = cc.getDeclaredMethod("copyMapToWorldsFolder");
    copy.setBody("{" +
      "this.id = this.dungeon.getInstances().size();" +
      "java.io.File target = this.getUniqueWorldName();" +
      "org.apache.commons.io.FileUtils.copyDirectory(this.getDungeon().getFolder(), target);" +
      "new java.io.File(target, \"config.yml\").delete();" +
      "new java.io.File(target, \"uid.dat\").delete();" +
      "new java.io.File(target, \"session.lock\").delete();" +
      "new java.io.File(target, \"data/paper/metadata.dat\").delete();" +
    "}");

    cc.writeFile(out);
    cc.detach();
  }
}
