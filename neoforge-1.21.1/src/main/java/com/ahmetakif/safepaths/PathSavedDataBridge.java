package com.ahmetakif.safepaths;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;

public abstract class PathSavedDataBridge extends SavedData {
    public static PathMemorySavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(PathMemorySavedData::new, PathSavedDataBridge::load), "safepaths_memory");
    }

    public static PathMemorySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        return PathMemorySavedData.readMemory(tag);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return writeMemory(tag);
    }

    public abstract CompoundTag writeMemory(CompoundTag tag);
}
