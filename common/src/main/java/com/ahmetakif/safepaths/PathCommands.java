package com.ahmetakif.safepaths;

import com.ahmetakif.safepaths.compat.MineColoniesProtection;
import com.ahmetakif.safepaths.config.PathConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

public final class PathCommands {
    private PathCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("safepaths").requires(source -> source.hasPermission(2))
                .then(Commands.literal("inspect")
                        .executes(context -> {
                            Player player = context.getSource().getPlayerOrException();
                            HitResult hit = player.pick(8.0, 0.0F, false);
                            BlockPos pos = hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK
                                    ? blockHit.getBlockPos() : player.getOnPos();
                            return inspect(context.getSource(), pos);
                        })
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> inspect(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "pos"))))));
    }

    private static MutableComponent message(String key, String fallback, Object... args) {
        return Component.translatableWithFallback("safepaths.inspect." + key, fallback, args);
    }

    public static int inspect(CommandSourceStack source, BlockPos pos) {
        ServerLevel level = source.getLevel();
        if (!level.isLoaded(pos)) {
            source.sendFailure(message("unloaded", "That chunk is not loaded."));
            return 0;
        }
        BlockState state = level.getBlockState(pos);
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        PathMemorySavedData.PathData path = memory.getPath(pos);
        if (path != null && !state.is(path.targetPathBlock)) path = null;
        long now = level.getGameTime();
        PathMemorySavedData.TrampleData progress = memory.getTrample(pos);
        boolean activeProgress = progress != null && now - progress.firstTime <= PathConfig.CONSTRUCTION_TIME.get();
        int passes = activeProgress ? progress.count : 0;
        long window = activeProgress ? Math.max(0, PathConfig.CONSTRUCTION_TIME.get() - (now - progress.firstTime)) : PathConfig.CONSTRUCTION_TIME.get();
        MutableComponent result = message("header", "Safepaths at %s, %s, %s — %s", pos.getX(), pos.getY(), pos.getZ(), BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
        result.append("\n").append(message("status", "Status: %s", status(level, pos, state, path, source.getEntity() instanceof Player player ? player : null)));
        result.append("\n").append(message("progress", "Passes: %s/%s · construction window: %s s", passes, PathConfig.REQUIRED_PASSES.get(), window / 20));
        BlockState target = PathCreationEvent.determinePathState(state);
        if (!PathCreationEvent.isPathBlock(state) && target != null) {
            result.append("\n").append(message("target", "Target: %s", BuiltInRegistries.BLOCK.getKey(target.getBlock()).toString()));
        }
        result.append("\n").append(message("speed", "Road speed bonus: %s%%", String.format(Locale.ROOT, "%.2f", PathCreationEvent.speedMultiplier(level, pos, state) * 100)));
        if (path != null) {
            long remaining = Math.max(0, PathConfig.DECAY_TIME.get() - (now - path.lastTime));
            BlockState original = path.originalState == null ? Blocks.DIRT.defaultBlockState() : path.originalState;
            result.append("\n").append(message("decay", "Original terrain: %s · decay countdown: %s s", BuiltInRegistries.BLOCK.getKey(original.getBlock()).toString(), remaining / 20));
        }
        source.sendSuccess(() -> result, false);
        return 1;
    }

    private static Component status(ServerLevel level, BlockPos pos, BlockState state, PathMemorySavedData.PathData path, Player player) {
        if (PathCreationEvent.isPathBlock(state)) {
            if (path == null) return message("permanent", "Road without a decay record");
            if (!PathConfig.ENABLE_PATH_DECAY.get()) return message("decay_disabled", "Decay is disabled; the path record is retained");
            BlockState original = path.originalState == null || path.originalState.isAir() ? Blocks.DIRT.defaultBlockState() : path.originalState;
            if (!PathCreationEvent.canRemainAt(level, pos, original)) return message("support", "Restoration is waiting for safe support");
            if (PathPlatform.isModLoaded("minecolonies") && !MineColoniesProtection.canDecay(level, pos, path)) {
                return message("permission", "MineColonies permissions prevent this change");
            }
            return message("tracked", "Tracked road; authorized traffic refreshes its decay countdown");
        }
        if (PathCreationEvent.isProtectedFromPathing(state)) return message("protected", "Protected terrain or block entity");
        if (!PathConfig.ENABLE_PATH_CREATION.get()) return message("creation_disabled", "Path creation is disabled");
        if (player != null && PathConfig.SNEAK_PREVENTS_PATH_CREATION.get() && player.isShiftKeyDown()) {
            return message("sneaking", "Sneaking prevents new construction passes");
        }
        if (player != null && !PathCreationEvent.canTrample(level, pos, player)) return message("permission", "MineColonies permissions prevent this change");
        BlockState target = PathCreationEvent.determinePathState(state);
        if (!PathCreationEvent.isPathable(state) || target == null) return message("unsupported", "No applicable path conversion");
        if (!PathCreationEvent.canRemainAt(level, pos, target)) return message("support", "Conversion is waiting for safe support");
        return message("ready", "Ready to count eligible footsteps");
    }
}
