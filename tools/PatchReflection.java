import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Disables MythicDungeons 2.0.1's legacy NMS memory-leak cleanup hack.
 *
 * The old hack searches legacy EntityPlayer/pathfinder fields that no longer
 * exist on Paper 26.1.2. Dungeon gameplay does not depend on it.
 */
public final class PatchReflection {
    public static void main(String[] args) throws Exception {
        ClassPool pool = new ClassPool(false);
        pool.appendSystemPath();
        pool.insertClassPath(args[0]);

        CtClass cc = pool.get(
                "net.playavalon.mythicdungeons.utility.helpers.ReflectionUtils");
        cc.defrost();

        CtMethod prep = cc.getDeclaredMethod("prepMemoryLeakKiller");
        prep.setBody(
                "{ versionSupported = false; entityWorldSupported = false; " +
                "entityForceCleanupSupported = false; return; }");

        cc.writeFile(args[1]);
        cc.detach();
    }
}
