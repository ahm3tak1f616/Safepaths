package com.ahmetakif.safepaths;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = "safepaths")
public final class PathEvents {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity living) PathCreationEvent.tickEntity(living);
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) PathCreationEvent.tickLevel(level);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            PathMemorySavedData.get(serverLevel).clearAt(event.getPos());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            PathMemorySavedData memory = PathMemorySavedData.get(level);
            if (event instanceof BlockEvent.EntityMultiPlaceEvent multiple) {
                multiple.getReplacedBlockSnapshots().forEach(snapshot -> memory.clearAt(snapshot.getPos()));
            } else {
                memory.clearAt(event.getPos());
            }
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPiston(PistonEvent.Pre event) {
        LevelAccessor level = event.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            var resolver = event.getStructureHelper();
            if (event.isCanceled() || resolver == null || !resolver.resolve()) {
                return;
            }
            PathMemorySavedData memory = PathMemorySavedData.get(serverLevel);
            BlockPos pos = event.getPos();
            memory.clearAt(pos);
            memory.clearAt(pos.relative(event.getDirection()));
            var movement = event.getPistonMoveType().isExtend ? event.getDirection() : event.getDirection().getOpposite();
            for (BlockPos moved : resolver.getToPush()) {
                memory.clearAt(moved);
                memory.clearAt(moved.relative(movement));
            }
            for (BlockPos destroyed : resolver.getToDestroy()) {
                memory.clearAt(destroyed);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        PathCreationEvent.unloadLevel(event.getLevel());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        PathCreationEvent.stopServer();
    }
}
