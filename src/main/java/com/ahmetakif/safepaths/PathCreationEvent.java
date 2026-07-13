package com.ahmetakif.safepaths;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = "safepaths")
public class PathCreationEvent {
    public static final TagKey<Block> PATHABLE_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath("safepaths", "can_become_path"));
    public static final TagKey<Block> CANNOT_BECOME_PATH = BlockTags.create(ResourceLocation.fromNamespaceAndPath("safepaths", "cannot_become_path"));
    public static final TagKey<EntityType<?>> PATH_CREATORS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("safepaths", "path_creators"));
    public static final TagKey<EntityType<?>> PATH_BLOCKED = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("safepaths", "path_blocked"));

    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("safepaths", "path_speed");
    private static final Map<Entity, BlockPos> LAST_POSITIONS = new WeakHashMap<>();

    private static boolean isPathBlock(BlockState state) {
        return state.is(Blocks.DIRT_PATH);
    }

    /**
     * Players always qualify. MineColonies (and datapack) entities use tags;
     * barbarian-named raiders stay blocked even if not listed in the tag.
     */
    private static boolean isPathRelevantEntity(LivingEntity livingEntity) {
        if (livingEntity instanceof Player) {
            return true;
        }
        EntityType<?> type = livingEntity.getType();
        if (type.is(PATH_BLOCKED)) {
            return false;
        }
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (entityId != null && entityId.getPath().contains("barbarian")) {
            return false;
        }
        return type.is(PATH_CREATORS);
    }

    private static boolean isProtectedFromPathing(BlockState state) {
        if (state.is(Blocks.FARMLAND) || state.is(CANNOT_BECOME_PATH)) {
            return true;
        }
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (blockId == null) {
            return false;
        }
        return blockId.getPath().contains("farmland") || blockId.getNamespace().equals("minecolonies");
    }

    private static void syncSpeedBoost(LivingEntity livingEntity, BlockState stateBelow) {
        AttributeInstance attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null) {
            return;
        }

        AttributeModifier existing = attribute.getModifier(SPEED_MODIFIER_ID);
        boolean shouldBoost = PathConfig.ENABLE_SPEED_BOOST.get() && isPathBlock(stateBelow);
        if (shouldBoost) {
            double desired = PathConfig.SPEED_MULTIPLIER.get();
            if (existing == null || Double.compare(existing.amount(), desired) != 0) {
                attribute.removeModifier(SPEED_MODIFIER_ID);
                attribute.addTransientModifier(new AttributeModifier(
                        SPEED_MODIFIER_ID,
                        desired,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        } else if (existing != null) {
            attribute.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    private static int cleanupIntervalTicks() {
        int construction = PathConfig.CONSTRUCTION_TIME.get();
        int decay = PathConfig.DECAY_TIME.get();
        return Math.max(20, Math.min(1000, Math.min(construction, decay) / 24));
    }

    private static void clearMemoryAt(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        PathMemorySavedData.get(serverLevel).clearAt(pos);
    }

    private static void clearMemoryAt(Level level, Iterable<BlockPos> positions) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        PathMemorySavedData data = PathMemorySavedData.get(serverLevel);
        for (BlockPos pos : positions) {
            data.clearAt(pos);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof Level level)) return;
        clearMemoryAt(level, event.getPos());
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level level)) return;
        clearMemoryAt(level, event.getPos());
    }

    @SubscribeEvent
    public static void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        if (!(event.getLevel() instanceof Level level)) return;
        clearMemoryAt(level, event.getPos());
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;
        clearMemoryAt(level, event.getAffectedBlocks());
    }

    @SubscribeEvent
    public static void onPiston(PistonEvent.Pre event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) return;
        clearMemoryAt(level, event.getFaceOffsetPos());
        var helper = event.getStructureHelper();
        if (helper != null && helper.resolve()) {
            clearMemoryAt(level, helper.getToPush());
            clearMemoryAt(level, helper.getToDestroy());
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) return;
        LAST_POSITIONS.entrySet().removeIf(entry -> entry.getKey().level() == level || entry.getKey().isRemoved());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        LAST_POSITIONS.clear();
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        long currentTick = level.getGameTime();
        int interval = cleanupIntervalTicks();
        if (currentTick % interval != 0) return;

        PathMemorySavedData memory = PathMemorySavedData.get(serverLevel);
        long decayTime = PathConfig.DECAY_TIME.get();
        long unloadGrace = decayTime * 2L;

        memory.removeTrampleIf(entry ->
                (currentTick - entry.getValue().firstTime) > PathConfig.CONSTRUCTION_TIME.get());

        memory.removePathIf(entry -> {
            BlockPos pos = entry.getKey();
            PathMemorySavedData.PathData pathData = entry.getValue();
            long age = currentTick - pathData.lastTime;

            if (!level.isLoaded(pos)) {
                // Drop very old unloaded entries so memory cannot grow forever;
                // the dirt path remains, but we stop tracking it.
                return age > unloadGrace;
            }

            BlockState current = level.getBlockState(pos);
            if (!current.is(pathData.targetPathBlock)) {
                return true;
            }

            if (age <= decayTime) {
                return false;
            }

            level.setBlockAndUpdate(pos, pathData.originalState);
            return true;
        });
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof LivingEntity livingEntity)) return;

        // Cheap allow-list gate before any block lookups.
        if (!isPathRelevantEntity(livingEntity)) return;

        BlockPos posBelow = livingEntity.getOnPos();
        BlockState stateBelow = level.getBlockState(posBelow);
        if (stateBelow.isAir() || isProtectedFromPathing(stateBelow)) return;

        syncSpeedBoost(livingEntity, stateBelow);

        if (!livingEntity.onGround()) return;

        long currentTick = level.getGameTime();

        if (posBelow.equals(LAST_POSITIONS.get(livingEntity))) return;
        LAST_POSITIONS.put(livingEntity, posBelow.immutable());

        PathMemorySavedData memory = PathMemorySavedData.get(serverLevel);

        if (isPathBlock(stateBelow)) {
            PathMemorySavedData.PathData pathData = memory.getPath(posBelow);
            if (pathData != null) {
                if (!stateBelow.is(pathData.targetPathBlock)) {
                    memory.removePath(posBelow);
                } else {
                    pathData.lastTime = currentTick;
                    memory.touch();
                }
            }
            return;
        }

        if (stateBelow.is(PATHABLE_BLOCKS)) {
            PathMemorySavedData.TrampleData data = memory.getTrample(posBelow);
            if (data == null) {
                data = new PathMemorySavedData.TrampleData(0, currentTick);
            }
            data.count++;

            if (data.count >= PathConfig.REQUIRED_PASSES.get()) {
                BlockState targetState = Blocks.DIRT_PATH.defaultBlockState();
                memory.putPath(posBelow, new PathMemorySavedData.PathData(stateBelow, targetState.getBlock(), currentTick));
                level.setBlockAndUpdate(posBelow, targetState);
                memory.removeTrample(posBelow);
            } else {
                memory.putTrample(posBelow, data);
            }
        } else {
            memory.clearAt(posBelow);
        }
    }
}
