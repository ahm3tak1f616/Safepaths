package com.ahmetakif.safepaths;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class PathSavedDataBridge extends SavedData {
    public static PathMemorySavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                PathSavedDataBridge::load, PathMemorySavedData::new, "safepaths_memory");
    }

    public static PathMemorySavedData load(CompoundTag tag) {
        return PathMemorySavedData.readMemory(tag);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return writeMemory(tag);
    }

    public abstract CompoundTag writeMemory(CompoundTag tag);
}
