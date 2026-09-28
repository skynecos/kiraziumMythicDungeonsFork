import javassist.*;

public final class PatchPart42Cleanup {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) throw new IllegalArgumentException("<input-jar> <paper-api> <out-dir>");
        ClassPool pool = new ClassPool(true);
        pool.insertClassPath(args[0]);
        pool.insertClassPath(args[1]);

        CtClass cc = pool.get("net.playavalon.mythicdungeons.listeners.AvalonListener");
        cc.defrost();
        CtMethod m = cc.getDeclaredMethod("onPlayerJoin");
        m.insertAfter("{" +
            "org.bukkit.entity.Player p = $1.getPlayer();" +
            "net.playavalon.mythicdungeons.player.MythicPlayer mp = " +
            "net.playavalon.mythicdungeons.MythicDungeons.inst().getMythicPlayer(p);" +
            "if (mp != null && mp.getInstance() == null && mp.getSavedPosition() != null) {" +
            "  mp.setSavedPosition(null);" +
            "  mp.clearExitLocation();" +
            "}" +
        "}");
        cc.writeFile(args[2]);
        cc.detach();
    }
}
