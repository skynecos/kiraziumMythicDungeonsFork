import javassist.*;
import javassist.expr.*;

public final class PatchLeaveFallback {
    public static void main(String[] args) throws Exception {
        ClassPool pool = new ClassPool(true);
        pool.insertClassPath(args[0]);
        pool.insertClassPath(args[1]);
        pool.insertClassPath(args[2]);

        CtClass cc = pool.get("net.playavalon.mythicdungeons.commands.dungeon.LeaveCommand");
        cc.defrost();
        CtMethod m = cc.getDeclaredMethod("onCommand");
        m.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall call) throws CannotCompileException {
                if (call.getClassName().equals("org.bukkit.entity.Player")
                        && call.getMethodName().equals("getRespawnLocation")) {
                    call.replace(
                        "{ $_ = net.playavalon.mythicdungeons.utility.helpers.Part42Compat" +
                        ".safeExitFallback($0); }"
                    );
                }
            }
        });
        cc.writeFile(args[3]);
        cc.detach();
    }
}
