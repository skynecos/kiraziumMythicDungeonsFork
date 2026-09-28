import javassist.*;
import javassist.expr.*;
public final class PatchLeaveCancellation {
  public static void main(String[] a) throws Exception {
    ClassPool p = new ClassPool(true);
    p.insertClassPath(a[0]);
    p.insertClassPath(a[1]);
    CtClass c = p.get("net.playavalon.mythicdungeons.api.parents.instances.InstancePlayable");
    c.defrost();
    CtMethod m = c.getDeclaredMethod("removePlayer", new CtClass[]{p.get("net.playavalon.mythicdungeons.player.MythicPlayer"), CtClass.booleanType});
    m.instrument(new ExprEditor(){
      public void edit(MethodCall x) throws CannotCompileException {
        if (x.getClassName().equals("net.playavalon.mythicdungeons.api.parents.instances.AbstractInstance") && x.getMethodName().equals("removePlayer")) {
          x.replace("{ $proceed($$); if (this.players.contains($1)) { return; } }");
        }
      }
    });
    c.writeFile(a[2]);
    c.detach();
  }
}
