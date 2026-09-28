package net.playavalon.mythicdungeons.utility.helpers;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/** Paper 26.1.2 player cleanup/recovery compatibility helpers. */
public final class Part42Compat {
    private Part42Compat() {}

    public static void persistIfOffline(Player player) {
        if (player != null && !player.isOnline()) {
            player.saveData();
        }
    }

    public static Location safeExitFallback(Player player) {
        Location respawn = player.getRespawnLocation();
        if (respawn != null) return respawn;

        List<World> worlds = Bukkit.getWorlds();
        if (!worlds.isEmpty()) return worlds.get(0).getSpawnLocation();

        return player.getLocation();
    }
}
