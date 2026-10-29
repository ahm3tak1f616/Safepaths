package com.ahmetakif.safepaths;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Predicate;

public class PathMemorySavedData extends PathSavedDataBridge {

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
        public UUID creatorId;
        public int colonyId;

        public PathData(BlockState originalState, Block targetPathBlock, long lastTime) {
            this.originalState = originalState;
            this.targetPathBlock = targetPathBlock;
            this.lastTime = lastTime;
        }
    }

    private final Map<BlockPos, TrampleData> trampleMemory = new HashMap<>();
    private final Map<BlockPos, PathData> pathMemory = new HashMap<>();
    private final Map<Long, Set<BlockPos>> pathsByChunk = new HashMap<>();

    public static PathMemorySavedData readMemory(CompoundTag tag) {
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
            if (compound.contains("originalState", Tag.TAG_COMPOUND)) {
                try {
                    originalState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), compound.getCompound("originalState"));
                } catch (IllegalArgumentException ignored) {
                    originalState = null;
                }
            }
            if ((originalState == null || originalState.isAir()) && compound.contains("original")) {
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
                    if (!block.defaultBlockState().isAir()) {
                        targetPathBlock = block;
                    }
                }
            }

            PathData path = new PathData(originalState, targetPathBlock, lastTime);
            path.creatorId = compound.hasUUID("creator") ? compound.getUUID("creator") : null;
            path.colonyId = compound.getInt("colony");
            data.putPath(pos, path);
        }

        return data;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public CompoundTag writeMemory(CompoundTag tag) {
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
            if (entry.getValue().creatorId != null) {
                compound.putUUID("creator", entry.getValue().creatorId);
            }
            compound.putInt("colony", entry.getValue().colonyId);

            if (entry.getValue().originalState != null) {
                ResourceLocation origKey = BuiltInRegistries.BLOCK.getKey(entry.getValue().originalState.getBlock());
                compound.putString("original", origKey.toString());
                compound.put("originalState", NbtUtils.writeBlockState(entry.getValue().originalState));
            }

            Block targetBlock = entry.getValue().targetPathBlock != null ? entry.getValue().targetPathBlock : Blocks.DIRT_PATH;
            ResourceLocation targetKey = BuiltInRegistries.BLOCK.getKey(targetBlock);
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
        BlockPos key = pos.immutable();
        pathMemory.put(key, data);
        pathsByChunk.computeIfAbsent(ChunkPos.asLong(key), ignored -> new HashSet<>()).add(key);
        setDirty();
    }

    public PathData getPath(BlockPos pos) {
        return pathMemory.get(pos);
    }

    public void clearAt(BlockPos pos) {
        boolean removedTrample = trampleMemory.remove(pos) != null;
        boolean removedPath = pathMemory.remove(pos) != null;
        if (removedPath) {
            removeChunkPosition(pos);
        }
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
        boolean changed = false;
        Iterator<Map.Entry<BlockPos, PathData>> entries = pathMemory.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<BlockPos, PathData> entry = entries.next();
            if (filter.test(entry)) {
                entries.remove();
                removeChunkPosition(entry.getKey());
                changed = true;
            }
        }
        if (changed) {
            setDirty();
        }
    }

    public void removeLoadedPathsIf(ServerLevel level, Predicate<Map.Entry<BlockPos, PathData>> filter) {
        boolean changed = false;
        Iterator<Map.Entry<Long, Set<BlockPos>>> chunks = pathsByChunk.entrySet().iterator();
        while (chunks.hasNext()) {
            Map.Entry<Long, Set<BlockPos>> chunk = chunks.next();
            if (!level.getChunkSource().hasChunk(ChunkPos.getX(chunk.getKey()), ChunkPos.getZ(chunk.getKey()))) {
                continue;
            }
            Iterator<BlockPos> positions = chunk.getValue().iterator();
            while (positions.hasNext()) {
                BlockPos pos = positions.next();
                if (filter.test(Map.entry(pos, pathMemory.get(pos)))) {
                    positions.remove();
                    pathMemory.remove(pos);
                    changed = true;
                }
            }
            if (chunk.getValue().isEmpty()) {
                chunks.remove();
            }
        }
        if (changed) {
            setDirty();
        }
    }

    private void removeChunkPosition(BlockPos pos) {
        long chunk = ChunkPos.asLong(pos);
        Set<BlockPos> positions = pathsByChunk.get(chunk);
        if (positions != null) {
            positions.remove(pos);
            if (positions.isEmpty()) {
                pathsByChunk.remove(chunk);
            }
        }
    }
}
