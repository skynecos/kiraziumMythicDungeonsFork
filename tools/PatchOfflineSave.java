import javassist.*;

public final class PatchOfflineSave {
    public static void main(String[] args) throws Exception {
        ClassPool pool = new ClassPool(true);
        pool.insertClassPath(args[0]);
        pool.insertClassPath(args[1]);
        pool.insertClassPath(args[2]);

        CtClass cc = pool.get("net.playavalon.mythicdungeons.api.parents.instances.InstancePlayable");
        cc.defrost();
        CtMethod m = cc.getDeclaredMethod("removePlayer", new CtClass[]{
            pool.get("net.playavalon.mythicdungeons.player.MythicPlayer"),
            CtClass.booleanType
        });
        m.insertAfter(
            "{ net.playavalon.mythicdungeons.utility.helpers.Part42Compat" +
            ".persistIfOffline($1.getPlayer()); }"
        );
        cc.writeFile(args[3]);
        cc.detach();
    }
}
