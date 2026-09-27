package net.playavalon.mythicdungeons.utility.helpers;

import java.util.Objects;
import org.bukkit.block.Block;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;

/**
 * Paper 26.1+ compatibility bridge for MythicDungeons' legacy embedded NBTEditor.
 *
 * MythicDungeons 2.0.1 uses this helper for block-entity NBT in dungeon
 * schematic capture/placement. NBT-API 2.16.1 supplies the tested MC26_1 NMS
 * layer, avoiding the removed legacy NMS names used by the original helper.
 */
public final class NBTEditor {
    private NBTEditor() {}

    public static String getVersion() {
        return "NBT-API-bridge-2.16.1";
    }

    public static NBTCompound getNBTCompound(Object object, Object... keys) {
        if (!(object instanceof Block block)) {
            return null;
        }
        if (keys != null && keys.length != 0) {
            throw new UnsupportedOperationException(
                    "Nested NBT paths are not used by MythicDungeons' schematic layer");
        }
        try {
            String snbt = NBT.get(block.getState(), nbt -> nbt.toString());
            if (snbt == null) return null;
            return NBTCompound.fromJson(snbt);
        } catch (Throwable ignored) {
            // Normal blocks do not have BlockEntity NBT. Legacy behavior is null.
            return null;
        }
    }

    public static NBTCompound getNBTCompound(String snbt) {
        return NBTCompound.fromJson(snbt);
    }

    public static NBTCompound getEmptyNBTCompound() {
        return NBTCompound.fromJson("{}");
    }

    @SuppressWarnings("unchecked")
    public static <T> T set(T object, Object value, Object... keys) {
        if (!(object instanceof Block block)) {
            return object;
        }
        if (value == null) {
            return object;
        }
        if (!(value instanceof NBTCompound compound)) {
            throw new IllegalArgumentException("Block NBT value must be an NBTCompound");
        }
        if (keys != null && keys.length != 0) {
            throw new UnsupportedOperationException(
                    "Nested NBT paths are not used by MythicDungeons' schematic layer");
        }

        try {
            final String snbt = compound.toJson();
            NBT.modify(block.getState(), nbt -> {
                ReadWriteNBT parsed = NBT.parseNBT(snbt);
                nbt.clearNBT();
                nbt.mergeCompound(parsed);
            });
        } catch (Throwable t) {
            throw new RuntimeException(
                    "Unable to apply BlockEntity NBT on Paper 26.1.2", t);
        }
        return object;
    }

    public static final class NBTCompound {
        protected final Object tag;

        protected NBTCompound(Object tag) {
            this.tag = Objects.requireNonNull(tag, "tag");
        }

        public void set(Object value, Object... keys) {
            if (!(tag instanceof ReadWriteNBT)) {
                throw new UnsupportedOperationException("NBT compound is not mutable");
            }
            if (keys == null || keys.length != 1 || !(keys[0] instanceof String)) {
                throw new UnsupportedOperationException(
                        "Only direct string keys are supported by the compatibility bridge");
            }
            // Kept for binary shape compatibility. MythicDungeons 2.0.1 itself
            // does not call this mutation method in its schematic path.
            throw new UnsupportedOperationException(
                    "Direct mutation is not used by MythicDungeons 2.0.1");
        }

        public String toJson() {
            return tag.toString();
        }

        public static NBTCompound fromJson(String snbt) {
            if (snbt == null || "null".equals(snbt)) {
                return null;
            }
            try {
                return new NBTCompound(NBT.parseNBT(snbt));
            } catch (Throwable t) {
                throw new IllegalArgumentException("Invalid SNBT data", t);
            }
        }

        @Override
        public String toString() {
            return toJson();
        }

        @Override
        public int hashCode() {
            return toJson().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof NBTCompound other)) return false;
            return toJson().equals(other.toJson());
        }
    }
}
