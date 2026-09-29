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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.List;
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
    private static final Map<Entity, Long> LAST_PATH_TICKS = new WeakHashMap<>();

    private static boolean isPathBlock(BlockState state) {
        return state.is(Blocks.DIRT_PATH);
    }

    private static boolean isPathRelevantEntity(LivingEntity livingEntity) {
        if (livingEntity instanceof Player) {
            return true;
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
        if (state.is(Blocks.FARMLAND) || state.is(Blocks.DIRT_PATH) || state.is(CANNOT_BECOME_PATH)) {
            return true;
        }
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return blockId.getPath().contains("farmland") || blockId.getNamespace().equals("minecolonies");
    }

    private static boolean isPathUnder(LivingEntity entity, Level level) {
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
                    BlockState bs = level.getBlockState(mpos);
                    if (isPathBlock(bs)) {
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
                || (livingEntity instanceof Player player && player.getAbilities().flying)
                || livingEntity.fallDistance >= 3.5F) {
            LAST_PATH_TICKS.remove(livingEntity);
            if (attribute.getModifier(SPEED_MODIFIER_ID) != null) {
                attribute.removeModifier(SPEED_MODIFIER_ID);
            }
            return;
        }

        boolean onPath = isPathBlock(stateBelow) || isPathUnder(livingEntity, level);
        if (onPath) {
            LAST_PATH_TICKS.put(livingEntity, currentTick);
        }

        boolean shouldBoost = false;
        if (PathConfig.ENABLE_SPEED_BOOST.get()) {
            Long lastTick = LAST_PATH_TICKS.get(livingEntity);
            if (lastTick != null && currentTick - lastTick <= 15) {
                shouldBoost = true;
            }
        }

        AttributeModifier existing = attribute.getModifier(SPEED_MODIFIER_ID);
        if (shouldBoost) {
            double multiplier = PathConfig.SPEED_MULTIPLIER.get();
            if (existing == null || existing.amount() != multiplier) {
                if (existing != null) {
                    attribute.removeModifier(SPEED_MODIFIER_ID);
                }
                attribute.addTransientModifier(new AttributeModifier(
                        SPEED_MODIFIER_ID,
                        multiplier,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        } else if (existing != null) {
            attribute.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.isClientSide()) return;
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        memory.clearAt(event.getPos());
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        Level raw = event.getLevel();
        if (!(raw instanceof ServerLevel level) || level.isClientSide()) return;
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        for (BlockPos pos : event.getAffectedBlocks()) {
            memory.clearAt(pos);
        }
    }

    @SubscribeEvent
    public static void onPistonPre(PistonEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.isClientSide()) return;
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        BlockPos pos = event.getPos();
        memory.clearAt(pos);
        memory.clearAt(pos.relative(event.getDirection()));
    }

    @SubscribeEvent
    @SuppressWarnings("resource")
    public static void onLevelUnload(LevelEvent.Unload event) {
        LevelAccessor accessor = event.getLevel();
        if (accessor.isClientSide()) return;
        LAST_POSITIONS.entrySet().removeIf(entry -> entry.getKey().level() == accessor || entry.getKey().isRemoved());
        LAST_PATH_TICKS.entrySet().removeIf(entry -> entry.getKey().level() == accessor || entry.getKey().isRemoved());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        LAST_POSITIONS.clear();
        LAST_PATH_TICKS.clear();
    }

    private record RevertTarget(BlockPos pos, BlockState state) {}

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.isClientSide()) return;

        long currentTick = level.getGameTime();
        if (currentTick % 20 != 0) return;

        PathMemorySavedData memory = PathMemorySavedData.get(level);
        int constructionTicks = PathConfig.CONSTRUCTION_TIME.get() * 20;
        int decayTicks = PathConfig.DECAY_TIME.get() * 20;

        memory.removeTrampleIf(entry -> {
            PathMemorySavedData.TrampleData data = entry.getValue();
            return currentTick - data.firstTime > constructionTicks;
        });

        List<RevertTarget> toRevert = new ArrayList<>();
        memory.removePathIf(entry -> {
            BlockPos pos = entry.getKey();
            PathMemorySavedData.PathData data = entry.getValue();

            if (!level.isLoaded(pos)) {
                return currentTick - data.lastTime > 2L * decayTicks;
            }

            BlockState current = level.getBlockState(pos);
            if (!current.is(data.targetPathBlock)) {
                return true;
            }

            if (currentTick - data.lastTime <= decayTicks) {
                return false;
            }

            BlockState restoreState = data.originalState;
            if (restoreState == null || restoreState.isAir()) {
                restoreState = Blocks.DIRT.defaultBlockState();
            }
            toRevert.add(new RevertTarget(pos, restoreState));
            return true;
        });

        for (RevertTarget target : toRevert) {
            level.setBlockAndUpdate(target.pos(), target.state());
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof LivingEntity livingEntity)) return;

        if (livingEntity.isRemoved()) {
            LAST_POSITIONS.remove(livingEntity);
            LAST_PATH_TICKS.remove(livingEntity);
            return;
        }

        if (!isPathRelevantEntity(livingEntity)) return;

        BlockPos posBelow = livingEntity.getOnPos();
        BlockState stateBelow = level.getBlockState(posBelow);
        long currentTick = level.getGameTime();

        syncSpeedBoost(livingEntity, level, stateBelow, currentTick);

        if (!livingEntity.onGround()) return;

        if (posBelow.equals(LAST_POSITIONS.get(livingEntity))) return;
        LAST_POSITIONS.put(livingEntity, posBelow.immutable());

        PathMemorySavedData memory = PathMemorySavedData.get(serverLevel);

        if (stateBelow.is(Blocks.DIRT_PATH)) {
            PathMemorySavedData.PathData existing = memory.getPath(posBelow);
            if (existing != null) {
                existing.lastTime = currentTick;
                memory.setDirty();
            }
            return;
        }

        if (stateBelow.isAir() || isProtectedFromPathing(stateBelow)) return;

        if (stateBelow.is(PATHABLE_BLOCKS)) {
            PathMemorySavedData.TrampleData data = memory.getTrample(posBelow);
            if (data == null) {
                data = new PathMemorySavedData.TrampleData(1, currentTick);
            } else {
                data.count++;
            }

            if (data.count >= PathConfig.REQUIRED_PASSES.get()) {
                level.setBlockAndUpdate(posBelow, Blocks.DIRT_PATH.defaultBlockState());
                memory.removeTrample(posBelow);
                memory.putPath(posBelow, new PathMemorySavedData.PathData(stateBelow, Blocks.DIRT_PATH, currentTick));
            } else {
                memory.putTrample(posBelow, data);
            }
        } else if (memory.hasTrample(posBelow)) {
            memory.removeTrample(posBelow);
        }
    }
}
