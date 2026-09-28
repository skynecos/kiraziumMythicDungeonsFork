import javassist.*;
import javassist.expr.*;

public final class PatchLeaveCancellation {
  public static void main(String[] args) throws Exception {
    ClassPool pool = new ClassPool(true);
    pool.insertClassPath(args[0]);
    pool.insertClassPath(args[1]);

    CtClass cc = pool.get("net.playavalon.mythicdungeons.api.parents.instances.InstancePlayable");
    cc.defrost();

    CtMethod method = cc.getDeclaredMethod(
        "removePlayer",
        new CtClass[]{
            pool.get("net.playavalon.mythicdungeons.player.MythicPlayer"),
            CtClass.booleanType
        }
    );

    method.instrument(new ExprEditor() {
      public void edit(MethodCall call) throws CannotCompileException {
        if (call.getClassName().equals(
              "net.playavalon.mythicdungeons.api.parents.instances.AbstractInstance")
            && call.getMethodName().equals("removePlayer")) {
          call.replace("{ $proceed($$); if (this.players.contains($1)) { return; } }");
        }
      }
    });

    cc.writeFile(args[2]);
    cc.detach();
  }
}
