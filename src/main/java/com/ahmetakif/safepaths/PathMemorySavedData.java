package com.ahmetakif.safepaths;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PathMemorySavedData extends SavedData {
    private static final String DATA_NAME = "safepaths_memory";

    public static class TrampleData {
        public int count;
        public final long firstTime;

        public TrampleData(int count, long firstTime) {
            this.count = count;
            this.firstTime = firstTime;
        }
    }

    public static class PathData {
        public BlockState originalState;
        public Block targetPathBlock;
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
                DATA_NAME
        );
    }

    public static PathMemorySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PathMemorySavedData data = new PathMemorySavedData();

        ListTag trampleList = tag.getList("trample", Tag.TAG_COMPOUND);
        for (int i = 0; i < trampleList.size(); i++) {
            CompoundTag compound = trampleList.getCompound(i);
            BlockPos pos = BlockPos.of(compound.getLong("pos"));
            int count = compound.getInt("count");
            long firstTime = compound.getLong("firstTime");
            data.trampleMemory.put(pos, new TrampleData(count, firstTime));
        }

        ListTag pathList = tag.getList("paths", Tag.TAG_COMPOUND);
        for (int i = 0; i < pathList.size(); i++) {
            CompoundTag compound = pathList.getCompound(i);
            BlockPos pos = BlockPos.of(compound.getLong("pos"));
            long lastTime = compound.getLong("lastTime");

            BlockState originalState = null;
            if (compound.contains("original")) {
                String origId = compound.getString("original");
                ResourceLocation rl = ResourceLocation.tryParse(origId);
                if (rl != null) {
                    Block block = BuiltInRegistries.BLOCK.get(rl);
                    originalState = block.defaultBlockState();
                }
            }
            if (originalState == null || originalState.isAir()) {
                originalState = Blocks.DIRT.defaultBlockState();
            }

            Block targetPathBlock = Blocks.DIRT_PATH;
            if (compound.contains("target")) {
                String targetId = compound.getString("target");
                ResourceLocation rl = ResourceLocation.tryParse(targetId);
                if (rl != null) {
                    Block block = BuiltInRegistries.BLOCK.get(rl);
                    if (block != Blocks.AIR) {
                        targetPathBlock = block;
                    }
                }
            }

            data.pathMemory.put(pos, new PathData(originalState, targetPathBlock, lastTime));
        }

        return data;
    }

    @Override
    @SuppressWarnings("NullableProblems")
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
            compound.putLong("lastTime", entry.getValue().lastTime);

            if (entry.getValue().originalState != null) {
                ResourceLocation origKey = BuiltInRegistries.BLOCK.getKey(entry.getValue().originalState.getBlock());
                compound.putString("original", origKey.toString());
            }

            ResourceLocation targetKey = BuiltInRegistries.BLOCK.getKey(entry.getValue().targetPathBlock);
            compound.putString("target", targetKey.toString());

            pathList.add(compound);
        }
        tag.put("paths", pathList);

        return tag;
    }

    public void putTrample(BlockPos pos, TrampleData data) {
        trampleMemory.put(pos.immutable(), data);
        setDirty();
    }

    public TrampleData getTrample(BlockPos pos) {
        return trampleMemory.get(pos);
    }

    public boolean hasTrample(BlockPos pos) {
        return trampleMemory.containsKey(pos);
    }

    public void removeTrample(BlockPos pos) {
        if (trampleMemory.remove(pos) != null) {
            setDirty();
        }
    }

    public void putPath(BlockPos pos, PathData data) {
        pathMemory.put(pos.immutable(), data);
        setDirty();
    }

    public PathData getPath(BlockPos pos) {
        return pathMemory.get(pos);
    }

    public void clearAt(BlockPos pos) {
        boolean removedTrample = trampleMemory.remove(pos) != null;
        boolean removedPath = pathMemory.remove(pos) != null;
        if (removedTrample || removedPath) {
            setDirty();
        }
    }

    public void removeTrampleIf(Predicate<Map.Entry<BlockPos, TrampleData>> filter) {
        if (trampleMemory.entrySet().removeIf(filter)) {
            setDirty();
        }
    }

    public void removePathIf(Predicate<Map.Entry<BlockPos, PathData>> filter) {
        if (pathMemory.entrySet().removeIf(filter)) {
            setDirty();
        }
    }
}
