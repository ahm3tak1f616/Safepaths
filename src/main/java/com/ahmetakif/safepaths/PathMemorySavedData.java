package com.ahmetakif.safepaths;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

public class PathMemorySavedData extends SavedData {
    public static final String ID = "safepaths_memory";

    public static final class TrampleData {
        public int count;
        public long firstTime;

        public TrampleData(int count, long firstTime) {
            this.count = count;
            this.firstTime = firstTime;
        }
    }

    public static final class PathData {
        public final BlockState originalState;
        public final Block targetPathBlock;
        public long lastTime;

        public PathData(BlockState originalState, Block targetPathBlock, long lastTime) {
            this.originalState = originalState;
            this.targetPathBlock = targetPathBlock;
            this.lastTime = lastTime;
        }
    }

    private final Map<BlockPos, TrampleData> trampleMemory = new HashMap<>();
    private final Map<BlockPos, PathData> pathMemory = new HashMap<>();

    public static PathMemorySavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(PathMemorySavedData::new, PathMemorySavedData::load),
                ID
        );
    }

    public static PathMemorySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PathMemorySavedData data = new PathMemorySavedData();
        var blockLookup = registries.lookupOrThrow(Registries.BLOCK);

        ListTag trampleList = tag.getList("trample", Tag.TAG_COMPOUND);
        for (int i = 0; i < trampleList.size(); i++) {
            CompoundTag entry = trampleList.getCompound(i);
            BlockPos pos = BlockPos.of(entry.getLong("pos"));
            data.trampleMemory.put(pos, new TrampleData(entry.getInt("count"), entry.getLong("firstTime")));
        }

        ListTag pathList = tag.getList("paths", Tag.TAG_COMPOUND);
        for (int i = 0; i < pathList.size(); i++) {
            CompoundTag entry = pathList.getCompound(i);
            BlockPos pos = BlockPos.of(entry.getLong("pos"));
            BlockState original = NbtUtils.readBlockState(blockLookup, entry.getCompound("original"));
            Block target = Blocks.DIRT_PATH;
            if (entry.contains("target", Tag.TAG_STRING)) {
                ResourceLocation id = ResourceLocation.tryParse(entry.getString("target"));
                if (id != null) {
                    target = BuiltInRegistries.BLOCK.get(id);
                }
            }
            data.pathMemory.put(pos, new PathData(original, target, entry.getLong("lastTime")));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag trampleList = new ListTag();
        for (Map.Entry<BlockPos, TrampleData> entry : trampleMemory.entrySet()) {
            CompoundTag compound = new CompoundTag();
            compound.putLong("pos", entry.getKey().asLong());
            compound.putInt("count", entry.getValue().count);
            compound.putLong("firstTime", entry.getValue().firstTime);
            trampleList.add(compound);
        }
        tag.put("trample", trampleList);

        ListTag pathList = new ListTag();
        for (Map.Entry<BlockPos, PathData> entry : pathMemory.entrySet()) {
            CompoundTag compound = new CompoundTag();
            compound.putLong("pos", entry.getKey().asLong());
            compound.put("original", NbtUtils.writeBlockState(entry.getValue().originalState));
            ResourceLocation targetId = BuiltInRegistries.BLOCK.getKey(entry.getValue().targetPathBlock);
            compound.putString("target", targetId.toString());
            compound.putLong("lastTime", entry.getValue().lastTime);
            pathList.add(compound);
        }
        tag.put("paths", pathList);

        return tag;
    }

    public TrampleData getTrample(BlockPos pos) {
        return trampleMemory.get(pos);
    }

    public void putTrample(BlockPos pos, TrampleData data) {
        trampleMemory.put(pos.immutable(), data);
        setDirty();
    }

    public void removeTrample(BlockPos pos) {
        if (trampleMemory.remove(pos) != null) {
            setDirty();
        }
    }

    public PathData getPath(BlockPos pos) {
        return pathMemory.get(pos);
    }

    public void putPath(BlockPos pos, PathData data) {
        pathMemory.put(pos.immutable(), data);
        setDirty();
    }

    public void removePath(BlockPos pos) {
        if (pathMemory.remove(pos) != null) {
            setDirty();
        }
    }

    public void clearAt(BlockPos pos) {
        boolean removed = trampleMemory.remove(pos) != null;
        removed |= pathMemory.remove(pos) != null;
        if (removed) {
            setDirty();
        }
    }

    public boolean removeTrampleIf(Predicate<Map.Entry<BlockPos, TrampleData>> predicate) {
        boolean changed = false;
        Iterator<Map.Entry<BlockPos, TrampleData>> iterator = trampleMemory.entrySet().iterator();
        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                iterator.remove();
                changed = true;
            }
        }
        if (changed) {
            setDirty();
        }
        return changed;
    }

    public boolean removePathIf(Predicate<Map.Entry<BlockPos, PathData>> predicate) {
        boolean changed = false;
        Iterator<Map.Entry<BlockPos, PathData>> iterator = pathMemory.entrySet().iterator();
        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                iterator.remove();
                changed = true;
            }
        }
        if (changed) {
            setDirty();
        }
        return changed;
    }

    public void touch() {
        setDirty();
    }
}
