package com.ahmetakif.safepaths;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = "safepaths")
public class PathCreationEvent {
    public static final TagKey<Block> PATHABLE_BLOCKS = BlockTags.create(new ResourceLocation("safepaths", "can_become_path"));
    public static final TagKey<Block> CANNOT_BECOME_PATH = BlockTags.create(new ResourceLocation("safepaths", "cannot_become_path"));
    public static final TagKey<Block> PATH_BLOCKS = BlockTags.create(new ResourceLocation("safepaths", "is_path"));
    public static final TagKey<EntityType<?>> PATH_CREATORS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("safepaths", "path_creators"));
    public static final TagKey<EntityType<?>> PATH_BLOCKED = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("safepaths", "path_blocked"));

    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("c8b7f8e1-5e72-4d1a-8c3b-7f1a9e8d2c4e");
    private static final String SPEED_MODIFIER_NAME = "safepaths:path_speed";
    private static final Map<Entity, BlockPos> LAST_POSITIONS = new WeakHashMap<>();
    private static final Map<Entity, Long> LAST_PATH_TICKS = new WeakHashMap<>();

    private static List<? extends String> lastRawConversions = null;
    private static Map<Block, Block> customConversionMap = Collections.emptyMap();
    private static Set<Block> customTargetBlocks = Collections.emptySet();
    private static Set<Block> speedOnlyBlocks = Collections.emptySet();

    private static void updateConversionCache() {
        List<? extends String> currentRaw = PathConfig.CUSTOM_CONVERSIONS.get();
        if (currentRaw == lastRawConversions) {
            return;
        }
        lastRawConversions = currentRaw;
        Map<Block, Block> newMap = new LinkedHashMap<>();
        Set<Block> newTargets = new HashSet<>();
        Set<Block> newSpeedOnly = new HashSet<>();

        if (currentRaw != null) {
            for (String rawEntry : currentRaw) {
                if (rawEntry == null || !rawEntry.contains("->")) continue;

                String entry = rawEntry;
                if (rawEntry.contains("@")) {
                    String[] speedParts = rawEntry.split("@");
                    entry = speedParts[0].trim();
                }

                String[] parts = entry.split("->");
                if (parts.length != 2) continue;

                ResourceLocation sourceId = ResourceLocation.tryParse(parts[0].trim());
                ResourceLocation targetId = ResourceLocation.tryParse(parts[1].trim());

                if (sourceId != null && targetId != null
                        && BuiltInRegistries.BLOCK.containsKey(sourceId)
                        && BuiltInRegistries.BLOCK.containsKey(targetId)) {
                    Block src = BuiltInRegistries.BLOCK.get(sourceId);
                    Block tgt = BuiltInRegistries.BLOCK.get(targetId);
                    if (src != Blocks.AIR && tgt != Blocks.AIR) {
                        if (src == tgt) {
                            if (!newMap.containsKey(src)) {
                                newSpeedOnly.add(src);
                            }
                        } else {
                            newSpeedOnly.remove(src);
                            newMap.put(src, tgt);
                            newTargets.add(tgt);
                        }
                    }
                }
            }
        }
        customConversionMap = Collections.unmodifiableMap(newMap);
        customTargetBlocks = Collections.unmodifiableSet(newTargets);
        speedOnlyBlocks = Collections.unmodifiableSet(newSpeedOnly);
    }

    public static boolean isPathBlock(BlockState state) {
        if (state.is(Blocks.DIRT_PATH) || state.is(PATH_BLOCKS)) {
            return true;
        }
        updateConversionCache();
        if (speedOnlyBlocks.contains(state.getBlock()) || customTargetBlocks.contains(state.getBlock())) {
            return true;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id.getPath().endsWith("_path");
    }

    public static boolean isPathable(BlockState state) {
        if (state.is(PATHABLE_BLOCKS)) {
            return true;
        }
        updateConversionCache();
        return customConversionMap.containsKey(state.getBlock());
    }

    public static BlockState determinePathState(BlockState originalState) {
        updateConversionCache();
        if (speedOnlyBlocks.contains(originalState.getBlock())) {
            return null;
        }
        Block customTarget = customConversionMap.get(originalState.getBlock());
        if (customTarget != null) {
            return customTarget.defaultBlockState();
        }

        BlockState flattened = ShovelItem.getShovelPathingState(originalState);
        if (flattened != null && !flattened.isAir()) {
            return flattened;
        }

        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(originalState.getBlock());
        String name = id.getPath();
        if (name.contains("grass") || name.contains("dirt") || name.contains("soil") || name.contains("sand") || name.contains("gravel")) {
            return Blocks.DIRT_PATH.defaultBlockState();
        }

        return null;
    }

    private static boolean canCreatePath(LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            return !player.isSpectator();
        }
        EntityType<?> type = livingEntity.getType();
        if (type.is(PATH_BLOCKED)) {
            return false;
        }
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (entityId.getPath().contains("barbarian")) {
            return false;
        }
        return type.is(PATH_CREATORS);
    }

    private static boolean isProtectedFromPathing(BlockState state) {
        if (state.is(Blocks.FARMLAND) || isPathBlock(state) || state.is(CANNOT_BECOME_PATH)) {
            return true;
        }
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return blockId.getPath().contains("farmland") || blockId.getNamespace().equals("minecolonies");
    }

    private static boolean isEntityOnOrNearPath(LivingEntity entity, Level level) {
        AABB box = entity.getBoundingBox();
        int minX = Mth.floor(box.minX);
        int maxX = Mth.floor(box.maxX);
        int minZ = Mth.floor(box.minZ);
        int maxZ = Mth.floor(box.maxZ);
        int minY = Mth.floor(box.minY - 1.0);
        int maxY = Mth.floor(box.minY - 0.05);

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int y = maxY; y >= minY; y--) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mpos.set(x, y, z);
                    if (isPathBlock(level.getBlockState(mpos))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void syncSpeedBoost(LivingEntity livingEntity, Level level, BlockState stateBelow, long currentTick) {
        AttributeInstance attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null) {
            return;
        }

        if (livingEntity.isInWaterOrBubble() || livingEntity.isInLava() || livingEntity.isFallFlying()
                || (livingEntity instanceof Player player && (player.getAbilities().flying || player.isSpectator()))
                || livingEntity.fallDistance >= 3.5F) {
            LAST_PATH_TICKS.remove(livingEntity);
            if (attribute.getModifier(SPEED_MODIFIER_UUID) != null) {
                attribute.removeModifier(SPEED_MODIFIER_UUID);
            }
            return;
        }

        if (isPathBlock(stateBelow) || isEntityOnOrNearPath(livingEntity, level)) {
            LAST_PATH_TICKS.put(livingEntity, currentTick);
        }

        boolean shouldBoost = false;
        double multiplier = PathConfig.SPEED_MULTIPLIER.get();
        if (PathConfig.ENABLE_SPEED_BOOST.get() && multiplier > 0.0) {
            Long lastTick = LAST_PATH_TICKS.get(livingEntity);
            if (lastTick != null && currentTick - lastTick <= 15) {
                shouldBoost = true;
            }
        }

        AttributeModifier existing = attribute.getModifier(SPEED_MODIFIER_UUID);
        if (shouldBoost) {
            if (existing == null || Math.abs(existing.getAmount() - multiplier) > 0.0001) {
                if (existing != null) {
                    attribute.removeModifier(SPEED_MODIFIER_UUID);
                }
                attribute.addTransientModifier(new AttributeModifier(
                        SPEED_MODIFIER_UUID,
                        SPEED_MODIFIER_NAME,
                        multiplier,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }
        } else if (existing != null) {
            attribute.removeModifier(SPEED_MODIFIER_UUID);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity.level().isClientSide()) {
            return;
        }

        if (!livingEntity.isAlive() || livingEntity.isRemoved()) {
            LAST_POSITIONS.remove(livingEntity);
            LAST_PATH_TICKS.remove(livingEntity);
            AttributeInstance attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attribute != null && attribute.getModifier(SPEED_MODIFIER_UUID) != null) {
                attribute.removeModifier(SPEED_MODIFIER_UUID);
            }
            return;
        }

        Level level = livingEntity.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos posBelow = livingEntity.getOnPos();
        BlockState stateBelow = level.getBlockState(posBelow);
        long currentTick = level.getGameTime();

        syncSpeedBoost(livingEntity, level, stateBelow, currentTick);

        if (!canCreatePath(livingEntity)) {
            return;
        }

        PathMemorySavedData memory = PathMemorySavedData.get(serverLevel);

        if (isPathBlock(stateBelow)) {
            PathMemorySavedData.PathData existingDecay = memory.getPath(posBelow);
            if (existingDecay != null) {
                existingDecay.lastTime = currentTick;
                memory.setDirty();
            }
        }

        BlockPos lastPos = LAST_POSITIONS.get(livingEntity);
        if (lastPos == null || !lastPos.equals(posBelow)) {
            LAST_POSITIONS.put(livingEntity, posBelow);

            if (isProtectedFromPathing(stateBelow)) {
                return;
            }

            if (isPathable(stateBelow)) {
                handleTrample(serverLevel, memory, posBelow, stateBelow, currentTick);
            }
        }
    }

    private static void handleTrample(ServerLevel level, PathMemorySavedData memory, BlockPos posBelow, BlockState stateBelow, long currentTick) {
        PathMemorySavedData.TrampleData progress = memory.getTrample(posBelow);
        int steps = 1;
        long firstTime = currentTick;

        if (progress != null) {
            if (currentTick - progress.firstTime <= PathConfig.CONSTRUCTION_TIME.get()) {
                steps = progress.count + 1;
                firstTime = progress.firstTime;
            }
        }

        if (steps >= PathConfig.REQUIRED_PASSES.get()) {
            BlockState pathState = determinePathState(stateBelow);
            if (pathState != null) {
                level.setBlockAndUpdate(posBelow, pathState);
                memory.putPath(posBelow, new PathMemorySavedData.PathData(stateBelow, pathState.getBlock(), currentTick));
                memory.removeTrample(posBelow);
            }
        } else {
            memory.putTrample(posBelow, new PathMemorySavedData.TrampleData(steps, firstTime));
        }
    }

    private record RevertEntry(BlockPos pos, BlockState state) {}

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.level instanceof ServerLevel serverLevel)) {
            return;
        }

        long currentTick = serverLevel.getGameTime();
        if (currentTick % 20 != 0) {
            return;
        }

        PathMemorySavedData memory = PathMemorySavedData.get(serverLevel);
        int constructionTime = PathConfig.CONSTRUCTION_TIME.get();
        int decayTime = PathConfig.DECAY_TIME.get();

        memory.removeTrampleIf(entry -> currentTick - entry.getValue().firstTime > constructionTime);

        List<RevertEntry> toRevert = new ArrayList<>();
        memory.removePathIf(entry -> {
            BlockPos pos = entry.getKey();
            PathMemorySavedData.PathData data = entry.getValue();

            if (!serverLevel.isLoaded(pos)) {
                return false;
            }

            BlockState currentState = serverLevel.getBlockState(pos);
            if (!currentState.is(data.targetPathBlock) && !isPathBlock(currentState)) {
                return true;
            }

            if (currentTick - data.lastTime > decayTime) {
                BlockState restoreState = data.originalState;
                if (restoreState == null || restoreState.isAir()) {
                    restoreState = Blocks.DIRT.defaultBlockState();
                }
                toRevert.add(new RevertEntry(pos, restoreState));
                return true;
            }

            return false;
        });

        for (RevertEntry entry : toRevert) {
            serverLevel.setBlockAndUpdate(entry.pos(), entry.state());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            PathMemorySavedData.get(serverLevel).clearAt(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            PathMemorySavedData memory = PathMemorySavedData.get(serverLevel);
            for (BlockPos pos : event.getAffectedBlocks()) {
                memory.clearAt(pos);
            }
        }
    }

    @SubscribeEvent
    public static void onPiston(PistonEvent.Pre event) {
        LevelAccessor level = event.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            PathMemorySavedData memory = PathMemorySavedData.get(serverLevel);
            BlockPos pos = event.getPos();
            memory.clearAt(pos);
            memory.clearAt(pos.relative(event.getDirection()));
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        LevelAccessor accessor = event.getLevel();
        if (!accessor.isClientSide()) {
            LAST_POSITIONS.entrySet().removeIf(entry -> entry.getKey().level() == accessor || entry.getKey().isRemoved());
            LAST_PATH_TICKS.entrySet().removeIf(entry -> entry.getKey().level() == accessor || entry.getKey().isRemoved());
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        LAST_POSITIONS.clear();
        LAST_PATH_TICKS.clear();
        lastRawConversions = null;
    }
}
