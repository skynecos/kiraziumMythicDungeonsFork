import javassist.*;

public final class PatchAvalonListener2612 {
  public static void main(String[] args) throws Exception {
    if (args.length != 3) throw new IllegalArgumentException("usage: <input-jar> <paper-api-jar> <out-dir>");
    ClassPool pool = new ClassPool(true);
    pool.insertClassPath(args[0]);
    pool.insertClassPath(args[1]);
    CtClass cc = pool.get("net.playavalon.mythicdungeons.listeners.AvalonListener");
    cc.defrost();
    CtMethod m = cc.getDeclaredMethod("onLoadDungeonWorld");
    m.setBody("{" +
      "org.bukkit.World w = $1.getWorld();" +
      "java.lang.String name = w.getWorldFolder().getName();" +
      "java.util.regex.Pattern p = java.util.regex.Pattern.compile(\"(_[0-9]*)?$\");" +
      "java.lang.String dungeonName = p.matcher(name).replaceFirst(\"\");" +
      "if (net.playavalon.mythicdungeons.MythicDungeons.inst().getDungeons().get(dungeonName) == null) return;" +
      "w.setKeepSpawnInMemory(false);" +
      "w.setAutoSave(false);" +
    "}");
    cc.writeFile(args[2]);
    cc.detach();
  }
}
