package net.playavalon.mythicdungeons.utility.helpers;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

/**
 * Paper 26.1.2 compatible custom-head helper.
 * Replaces the legacy Authlib GameProfile + private profile-field reflection path.
 */
public class SkullUtil {
    public SkullUtil() {}

    public static ItemStack getNextButton() {
        return createHeadByTextures(
            Util.modernizeColors("&a&lNext Page"),
            "79f13daf-4884-40ab-8e35-95e472463321",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19"
        );
    }

    public static ItemStack getPreviousButton() {
        return createHeadByTextures(
            Util.modernizeColors("&a&lPrevious Page"),
            "5fecc571-bcbb-4aaa-b53c-b5d8715dbe37",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="
        );
    }

    public static ItemStack createHeadByTextures(String displayName, String uuid, String texture) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.fromString(uuid));
            profile.getProperties().add(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(profile);
            skullMeta.displayName(Util.modernizeColorsComponent(displayName));
            item.setItemMeta(skullMeta);
        } else if (meta != null) {
            meta.displayName(Util.modernizeColorsComponent(displayName));
            item.setItemMeta(meta);
        }
        return item;
    }
}
