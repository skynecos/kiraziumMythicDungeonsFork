package net.playavalon.mythicdungeons.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

/**
 * Paper 26.1.2-aware replacement for MythicDungeons' legacy version gate.
 */
public enum ServerVersion {
    v1_16, v1_16_1, v1_16_2, v1_16_3, v1_16_4, v1_16_5,
    v1_17, v1_17_1,
    v1_18, v1_18_1, v1_18_2,
    v1_19, v1_19_1, v1_19_2, v1_19_3, v1_19_4,
    v1_20, v1_20_1, v1_20_2, v1_20_3, v1_20_4, v1_20_5, v1_20_6,
    v1_21, v1_21_1, v1_21_3, v1_21_4, v1_21_5, v1_21_6, v1_21_7, v1_21_8,
    v26_1, v26_1_1, v26_1_2;

    private static final Pattern MC_VERSION = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
    private static final ServerVersion VERSION = detectVersion();
    private static final Platform PLATFORM = detectPlatform();

    private final int major;
    private final int minor;
    private final int revision;

    ServerVersion() {
        String[] parts = name().substring(1).split("_");
        major = Integer.parseInt(parts[0]);
        minor = Integer.parseInt(parts[1]);
        revision = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
    }

    private static ServerVersion detectVersion() {
        String raw = Bukkit.getServer().getBukkitVersion();
        Matcher matcher = MC_VERSION.matcher(raw);
        if (!matcher.find()) {
            throw new IllegalStateException("Unsupported server version: " + raw);
        }
        String name = "v" + matcher.group(1) + "_" + matcher.group(2)
                + (matcher.group(3) == null ? "" : "_" + matcher.group(3));
        try {
            return valueOf(name);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Unsupported server version: " + raw + " (parsed " + name + ")", ex);
        }
    }

    private static Platform detectPlatform() {
        try {
            Class.forName("io.papermc.paper.ServerBuildInfo");
            return Platform.PAPER;
        } catch (ClassNotFoundException ignored) {
            try {
                Class.forName("io.papermc.paper.configuration.Configuration");
                return Platform.PAPER;
            } catch (ClassNotFoundException ignoredAgain) {
                return Platform.OTHER;
            }
        }
    }

    public static ServerVersion get() {
        return VERSION;
    }

    public static boolean isPaper() {
        return PLATFORM == Platform.PAPER;
    }

    private int compare(ServerVersion other) {
        int c = Integer.compare(major, other.major);
        if (c != 0) return c;
        c = Integer.compare(minor, other.minor);
        if (c != 0) return c;
        return Integer.compare(revision, other.revision);
    }

    public boolean isBefore(ServerVersion other) {
        return compare(other) < 0;
    }

    public boolean isAfter(ServerVersion other) {
        return compare(other) > 0;
    }

    public boolean isAfterOrEqual(ServerVersion other) {
        return compare(other) >= 0;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + revision;
    }

    public static ServerVersion oldest() {
        return values()[0];
    }

    public static ServerVersion newest() {
        return values()[values().length - 1];
    }

    public enum Platform {
        PAPER,
        OTHER
    }
}
