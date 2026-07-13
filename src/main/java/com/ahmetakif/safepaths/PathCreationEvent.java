package com.ahmetakif.safepaths;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
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
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = "safepaths")
public class PathCreationEvent {
    public static final TagKey<Block> PATHABLE_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath("safepaths", "can_become_path"));
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("safepaths", "path_speed");

    private record DimPos(ResourceKey<Level> dimension, BlockPos pos) {
        static DimPos of(Level level, BlockPos pos) {
            return new DimPos(level.dimension(), pos.immutable());
        }
    }

    private static class TrampleData { int count; long firstTime; TrampleData(int count, long firstTime) { this.count = count; this.firstTime = firstTime; } }
    private static class PathData { BlockState originalState; Block targetPathBlock; long lastTime; PathData(BlockState originalState, Block targetPathBlock, long lastTime) { this.originalState = originalState; this.targetPathBlock = targetPathBlock; this.lastTime = lastTime; } }

    private static final Map<DimPos, TrampleData> TRAMPLE_MEMORY = new ConcurrentHashMap<>();
    private static final Map<DimPos, PathData> PATH_MEMORY = new ConcurrentHashMap<>();
    private static final Map<Entity, BlockPos> LAST_POSITIONS = new WeakHashMap<>();

    private static boolean isPathBlock(BlockState state) {
        return state.is(Blocks.DIRT_PATH);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof Level level)) return;
        DimPos key = DimPos.of(level, event.getPos());
        TRAMPLE_MEMORY.remove(key);
        PATH_MEMORY.remove(key);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level level)) return;
        DimPos key = DimPos.of(level, event.getPos());
        TRAMPLE_MEMORY.remove(key);
        PATH_MEMORY.remove(key);
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        long currentTick = level.getGameTime();
        if (currentTick % 1000 != 0) return;

        ResourceKey<Level> dimension = level.dimension();

        TRAMPLE_MEMORY.entrySet().removeIf(entry ->
                entry.getKey().dimension().equals(dimension)
                        && (currentTick - entry.getValue().firstTime) > PathConfig.CONSTRUCTION_TIME.get());

        PATH_MEMORY.entrySet().removeIf(entry -> {
            if (!entry.getKey().dimension().equals(dimension)) {
                return false;
            }
            if ((currentTick - entry.getValue().lastTime) <= PathConfig.DECAY_TIME.get()) {
                return false;
            }

            BlockPos pos = entry.getKey().pos();
            if (!level.isLoaded(pos)) {
                return false;
            }

            if (level.getBlockState(pos).is(entry.getValue().targetPathBlock)) {
                level.setBlockAndUpdate(pos, entry.getValue().originalState);
            }
            return true;
        });
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide() || !(entity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) entity;
        BlockPos posBelow = livingEntity.getOnPos();
        BlockState stateBelow = level.getBlockState(posBelow);
        if (stateBelow.isAir()) return;

        if (stateBelow.is(Blocks.FARMLAND)) return;

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(stateBelow.getBlock());
        if (blockId != null) {
            String bName = blockId.getPath();
            String bNamespace = blockId.getNamespace();

            if (bName.contains("farmland") || bNamespace.equals("minecolonies")) {
                return;
            }
        }

        boolean isAllowedEntity = (livingEntity instanceof Player);
        if (!isAllowedEntity) {
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType());
            if (entityId != null && entityId.getNamespace().equals("minecolonies")) {
                String p = entityId.getPath();
                if (p.contains("barbarian")) return;
                if (p.equals("citizen") || p.equals("visitor") || p.equals("mercenary")) isAllowedEntity = true;
            } else { return; }
        }

        AttributeInstance attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null && isAllowedEntity) {
            if (PathConfig.ENABLE_SPEED_BOOST.get() && isPathBlock(stateBelow)) {
                if (attribute.getModifier(SPEED_MODIFIER_ID) == null) {
                    AttributeModifier modifier = new AttributeModifier(SPEED_MODIFIER_ID, PathConfig.SPEED_MULTIPLIER.get(), AttributeModifier.Operation.ADD_VALUE);
                    attribute.addTransientModifier(modifier);
                }
            } else if (attribute.getModifier(SPEED_MODIFIER_ID) != null) {
                attribute.removeModifier(SPEED_MODIFIER_ID);
            }
        }

        long currentTick = level.getGameTime();

        if (posBelow.equals(LAST_POSITIONS.get(livingEntity))) return;
        LAST_POSITIONS.put(livingEntity, posBelow.immutable());

        DimPos key = DimPos.of(level, posBelow);

        if (isAllowedEntity && stateBelow.is(PATHABLE_BLOCKS)) {
            if (isPathBlock(stateBelow)) {
                PathData pathData = PATH_MEMORY.get(key);
                if (pathData != null) {
                    pathData.lastTime = currentTick;
                }
                return;
            }

            TrampleData data = TRAMPLE_MEMORY.getOrDefault(key, new TrampleData(0, currentTick));
            data.count++;

            if (data.count >= PathConfig.REQUIRED_PASSES.get()) {
                BlockState targetState = Blocks.DIRT_PATH.defaultBlockState();
                Block targetBlock = targetState.getBlock();

                PATH_MEMORY.put(key, new PathData(stateBelow, targetBlock, currentTick));
                level.setBlockAndUpdate(posBelow, targetState);
                TRAMPLE_MEMORY.remove(key);
            } else {
                TRAMPLE_MEMORY.put(key, data);
            }
        }
    }
}