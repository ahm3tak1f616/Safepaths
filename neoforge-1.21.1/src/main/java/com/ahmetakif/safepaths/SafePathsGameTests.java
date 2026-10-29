package com.ahmetakif.safepaths;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.level.PistonEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "removal"})
@GameTestHolder("safepaths")
@PrefixGameTestTemplate(false)
public class SafePathsGameTests {
    private static final BlockPos FLOOR = new BlockPos(2, 0, 2);
    private static final BlockPos DECAY_ROAD = new BlockPos(2, 2, 2);

    @GameTest(template = "platform")
    public static void grassIsPathable(GameTestHelper helper) {
        helper.assertTrue(
                Blocks.GRASS_BLOCK.defaultBlockState().is(PathCreationEvent.PATHABLE_BLOCKS),
                "Grass should be in #safepaths:can_become_path"
        );
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void dirtPathIsPathBlock(GameTestHelper helper) {
        helper.assertTrue(
                PathCreationEvent.isPathBlock(Blocks.DIRT_PATH.defaultBlockState()),
                "Dirt path should be recognized as a path block"
        );
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void farmlandCannotBecomePath(GameTestHelper helper) {
        BlockState farmland = Blocks.FARMLAND.defaultBlockState();
        helper.assertTrue(
                farmland.is(PathCreationEvent.CANNOT_BECOME_PATH),
                "Farmland should be in #safepaths:cannot_become_path"
        );
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void pathMemoryStoresAndClears(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        BlockPos absolute = helper.absolutePos(FLOOR);

        memory.putPath(absolute, new PathMemorySavedData.PathData(
                Blocks.GRASS_BLOCK.defaultBlockState(),
                Blocks.DIRT_PATH,
                level.getGameTime()
        ));
        helper.assertTrue(memory.getPath(absolute) != null, "Path memory should store an entry");

        memory.clearAt(absolute);
        helper.assertTrue(memory.getPath(absolute) == null, "Path memory should clear an entry");
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void trampleMemoryStoresAndClears(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        BlockPos absolute = helper.absolutePos(FLOOR);

        memory.putTrample(absolute, new PathMemorySavedData.TrampleData(1, level.getGameTime()));
        helper.assertTrue(memory.hasTrample(absolute), "Trample memory should store an entry");

        memory.removeTrample(absolute);
        helper.assertTrue(!memory.hasTrample(absolute), "Trample memory should clear an entry");
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void determinePathStateDefaultsToDirtPath(GameTestHelper helper) {
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState path = PathCreationEvent.determinePathState(grass);
        helper.assertTrue(
                path.is(Blocks.DIRT_PATH),
                "Grass should determine into dirt path, got " + path.getBlock()
        );
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void moddedPathNamingHeuristic(GameTestHelper helper) {
        boolean recognized = PathCreationEvent.isPathBlock(Blocks.DIRT_PATH.defaultBlockState());
        helper.assertTrue(recognized, "Path block recognition must succeed");
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void selfMappedBlockIsPathBlock(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        try {
            PathConfig.CUSTOM_CONVERSIONS.set(List.of("minecraft:gravel -> minecraft:gravel"));
            boolean recognized = PathCreationEvent.isPathBlock(Blocks.GRAVEL.defaultBlockState());
            helper.assertTrue(recognized, "Self-mapped gravel should be recognized as a path block");
            BlockState pathState = PathCreationEvent.determinePathState(Blocks.GRAVEL.defaultBlockState());
            helper.assertTrue(pathState == null, "Self-mapped gravel must not convert into another block");
        } finally {
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
        }
        helper.succeed();
    }

    private static void createExpiredPath(GameTestHelper helper, Block original, Block below) {
        helper.setBlock(DECAY_ROAD.below(2), Blocks.DIRT);
        helper.setBlock(DECAY_ROAD.below(), below);
        helper.setBlock(DECAY_ROAD, Blocks.DIRT_PATH);
        PathMemorySavedData.get(helper.getLevel()).putPath(helper.absolutePos(DECAY_ROAD),
                new PathMemorySavedData.PathData(original.defaultBlockState(), Blocks.DIRT_PATH,
                        helper.getLevel().getGameTime() - PathConfig.DECAY_TIME.get() - 100));
    }

    private static void assertDecayDeferred(GameTestHelper helper, Block original) {
        helper.assertBlockPresent(Blocks.DIRT_PATH, DECAY_ROAD);
        PathMemorySavedData.PathData path = PathMemorySavedData.get(helper.getLevel()).getPath(helper.absolutePos(DECAY_ROAD));
        helper.assertTrue(path != null, "Deferred decay must retain its decay record");
        helper.assertTrue(path.originalState.is(original), "Deferred decay must preserve the original terrain");
    }

    private static void assertUnsupportedDecayDeferred(GameTestHelper helper, Block original, Block below) {
        createExpiredPath(helper, original, below);
        helper.runAfterDelay(60, () -> {
            assertDecayDeferred(helper, original);
            helper.assertBlockPresent(below, DECAY_ROAD.below());
            helper.succeed();
        });
    }

    private static void assertTerrainRestored(GameTestHelper helper, Block original, Block below) {
        createExpiredPath(helper, original, below);
        helper.runAfterDelay(60, () -> {
            helper.assertBlockPresent(original, DECAY_ROAD);
            helper.assertBlockPresent(below, DECAY_ROAD.below());
            helper.assertTrue(PathMemorySavedData.get(helper.getLevel()).getPath(helper.absolutePos(DECAY_ROAD)) == null,
                    "Successful restoration should clear the decay record");
            helper.succeed();
        });
    }

    @GameTest(template = "platform")
    public static void sandDecayDoesNotLeaveHole(GameTestHelper helper) {
        assertUnsupportedDecayDeferred(helper, Blocks.SAND, Blocks.AIR);
    }

    @GameTest(template = "platform")
    public static void redSandDecayDoesNotLeaveHole(GameTestHelper helper) {
        assertUnsupportedDecayDeferred(helper, Blocks.RED_SAND, Blocks.AIR);
    }

    @GameTest(template = "platform")
    public static void legacyGravelDecayDoesNotLeaveHole(GameTestHelper helper) {
        assertUnsupportedDecayDeferred(helper, Blocks.GRAVEL, Blocks.AIR);
    }

    @GameTest(template = "platform")
    public static void sandDecayOverWaterDoesNotLeaveHole(GameTestHelper helper) {
        assertUnsupportedDecayDeferred(helper, Blocks.SAND, Blocks.WATER);
    }

    @GameTest(template = "platform")
    public static void redSandDecayOverWaterDoesNotLeaveHole(GameTestHelper helper) {
        assertUnsupportedDecayDeferred(helper, Blocks.RED_SAND, Blocks.WATER);
    }

    @GameTest(template = "platform")
    public static void legacyGravelDecayOverWaterDoesNotLeaveHole(GameTestHelper helper) {
        assertUnsupportedDecayDeferred(helper, Blocks.GRAVEL, Blocks.WATER);
    }

    @GameTest(template = "platform")
    public static void dirtPathOverWaterRestoresDirt(GameTestHelper helper) {
        assertTerrainRestored(helper, Blocks.DIRT, Blocks.WATER);
    }

    @GameTest(template = "platform")
    public static void grassPathOverWaterRestoresGrass(GameTestHelper helper) {
        assertTerrainRestored(helper, Blocks.GRASS_BLOCK, Blocks.WATER);
    }

    @GameTest(template = "platform")
    public static void sandDecayOverLavaDoesNotLeaveHole(GameTestHelper helper) {
        assertUnsupportedDecayDeferred(helper, Blocks.SAND, Blocks.LAVA);
    }

    @GameTest(template = "platform")
    public static void sandDecayOverReplaceableBlockDoesNotLeaveHole(GameTestHelper helper) {
        assertUnsupportedDecayDeferred(helper, Blocks.SAND, Blocks.SHORT_GRASS);
    }

    @GameTest(template = "platform")
    public static void sandDecayResumesWhenSupportReturns(GameTestHelper helper) {
        createExpiredPath(helper, Blocks.SAND, Blocks.AIR);
        helper.runAfterDelay(40, () -> {
            assertDecayDeferred(helper, Blocks.SAND);
            helper.setBlock(DECAY_ROAD.below(), Blocks.STONE);
        });
        helper.runAfterDelay(80, () -> {
            helper.assertBlockPresent(Blocks.SAND, DECAY_ROAD);
            helper.assertTrue(PathMemorySavedData.get(helper.getLevel()).getPath(helper.absolutePos(DECAY_ROAD)) == null,
                    "Successful restoration should clear the decay record");
            helper.succeed();
        });
    }

    @GameTest(template = "platform")
    public static void supportedSandStillDecays(GameTestHelper helper) {
        assertTerrainRestored(helper, Blocks.SAND, Blocks.STONE);
    }

    @GameTest(template = "platform")
    public static void grassDecayDoesNotRequireSupport(GameTestHelper helper) {
        assertTerrainRestored(helper, Blocks.GRASS_BLOCK, Blocks.AIR);
    }

    @GameTest(template = "platform")
    public static void conversionRejectsAirAndLiquidTargets(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        try {
            for (String id : List.of("air", "cave_air", "void_air", "water", "lava")) {
                PathConfig.CUSTOM_CONVERSIONS.set(List.of("minecraft:dirt -> minecraft:" + id));
                BlockState result = PathCreationEvent.determinePathState(Blocks.DIRT.defaultBlockState());
                helper.assertTrue(result == null || (!result.isAir() && !result.liquid()),
                        "Unsafe conversion target must be rejected: " + id);
            }
        } finally {
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void firstConversionWinsWithoutUnusedRoadTargets(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        try {
            PathConfig.CUSTOM_CONVERSIONS.set(List.of(
                    "minecraft:dirt -> minecraft:oak_planks", "minecraft:dirt -> minecraft:bricks"));
            helper.assertTrue(PathCreationEvent.determinePathState(Blocks.DIRT.defaultBlockState()).is(Blocks.OAK_PLANKS),
                    "The first explicit rule must take priority");
            helper.assertTrue(!PathCreationEvent.isPathBlock(Blocks.BRICKS.defaultBlockState()),
                    "Unused targets must not grant road speed or path protection");
        } finally {
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void explicitConversionsOverrideSelfMappingInEitherOrder(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        try {
            for (List<String> rules : List.of(
                    List.of("minecraft:stone -> minecraft:stone", "minecraft:stone -> minecraft:bricks"),
                    List.of("minecraft:stone -> minecraft:bricks", "minecraft:stone -> minecraft:stone"))) {
                PathConfig.CUSTOM_CONVERSIONS.set(rules);
                helper.assertTrue(PathCreationEvent.determinePathState(Blocks.STONE.defaultBlockState()).is(Blocks.BRICKS),
                        "Explicit conversions must override self-mapping regardless of order");
            }
        } finally {
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void originalBlockPropertiesSurviveSaveLoad(GameTestHelper helper) {
        PathMemorySavedData original = new PathMemorySavedData();
        BlockState stairs = Blocks.OAK_STAIRS.defaultBlockState()
                .setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.WATERLOGGED, true);
        original.putPath(FLOOR, new PathMemorySavedData.PathData(stairs, Blocks.DIRT_PATH, 12));
        CompoundTag saved = original.save(new CompoundTag(), helper.getLevel().registryAccess());
        PathMemorySavedData loaded = PathMemorySavedData.load(saved, helper.getLevel().registryAccess());
        helper.assertTrue(loaded.getPath(FLOOR).originalState.equals(stairs),
                "Original block properties must survive a world restart");
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void legacyPathMemoryStillLoads(GameTestHelper helper) {
        PathMemorySavedData original = new PathMemorySavedData();
        original.putPath(FLOOR, new PathMemorySavedData.PathData(Blocks.SAND.defaultBlockState(), Blocks.DIRT_PATH, 12));
        CompoundTag saved = original.save(new CompoundTag(), helper.getLevel().registryAccess());
        saved.getList("paths", 10).getCompound(0).remove("originalState");
        PathMemorySavedData loaded = PathMemorySavedData.load(saved, helper.getLevel().registryAccess());
        helper.assertTrue(loaded.getPath(FLOOR).originalState.is(Blocks.SAND), "Existing world data must remain readable");
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void staleDecayDoesNotOverwriteReplacementRoad(GameTestHelper helper) {
        createExpiredPath(helper, Blocks.GRASS_BLOCK, Blocks.STONE);
        helper.setBlock(DECAY_ROAD, Blocks.GRAVEL);
        helper.runAfterDelay(60, () -> {
            helper.assertBlockPresent(Blocks.GRAVEL, DECAY_ROAD);
            helper.assertTrue(PathMemorySavedData.get(helper.getLevel()).getPath(helper.absolutePos(DECAY_ROAD)) == null,
                    "Replacement roads must invalidate old decay records");
            helper.succeed();
        });
    }

    private static void stepOn(GameTestHelper helper, BlockPos pos) {
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        BlockPos absolute = helper.absolutePos(pos);
        player.setPos(absolute.getX() + 0.5, absolute.getY() + 1, absolute.getZ() + 0.5);
        player.setOnGround(true);
        player.mainSupportingBlockPos = Optional.of(absolute);
        PathEvents.onEntityTick(new EntityTickEvent.Post(player));
    }

    @GameTest(template = "platform")
    public static void fallingConversionDoesNotEraseGroundOverWater(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        int passes = PathConfig.REQUIRED_PASSES.get();
        try {
            PathConfig.CUSTOM_CONVERSIONS.set(List.of("minecraft:dirt -> minecraft:sand"));
            PathConfig.REQUIRED_PASSES.set(1);
            helper.setBlock(DECAY_ROAD.below(2), Blocks.DIRT);
            helper.setBlock(DECAY_ROAD.below(), Blocks.WATER);
            helper.setBlock(DECAY_ROAD, Blocks.DIRT);
            stepOn(helper, DECAY_ROAD);
        } finally {
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
            PathConfig.REQUIRED_PASSES.set(passes);
        }
        helper.runAfterDelay(60, () -> {
            helper.assertBlockPresent(Blocks.DIRT, DECAY_ROAD);
            helper.succeed();
        });
    }

    @GameTest(template = "platform")
    public static void blockEntitiesCannotBeTrampled(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        int passes = PathConfig.REQUIRED_PASSES.get();
        try {
            PathConfig.CUSTOM_CONVERSIONS.set(List.of("minecraft:chest -> minecraft:dirt_path"));
            PathConfig.REQUIRED_PASSES.set(1);
            helper.setBlock(DECAY_ROAD.below(), Blocks.STONE);
            helper.setBlock(DECAY_ROAD, Blocks.CHEST);
            stepOn(helper, DECAY_ROAD);
            helper.assertBlockPresent(Blocks.CHEST, DECAY_ROAD);
        } finally {
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
            PathConfig.REQUIRED_PASSES.set(passes);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void pistonClearsWholeMovedChain(GameTestHelper helper) {
        BlockPos piston = new BlockPos(0, 2, 2);
        helper.setBlock(piston, Blocks.PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.EAST));
        PathMemorySavedData memory = PathMemorySavedData.get(helper.getLevel());
        for (int offset = 1; offset <= 3; offset++) {
            BlockPos road = piston.east(offset);
            helper.setBlock(road, Blocks.DIRT_PATH);
            memory.putPath(helper.absolutePos(road), new PathMemorySavedData.PathData(
                    Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT_PATH, helper.getLevel().getGameTime()));
        }
        PathEvents.onPiston(new PistonEvent.Pre(helper.getLevel(), helper.absolutePos(piston),
                Direction.EAST, PistonEvent.PistonMoveType.EXTEND));
        for (int offset = 1; offset <= 3; offset++) {
            helper.assertTrue(memory.getPath(helper.absolutePos(piston.east(offset))) == null,
                    "Every moved block must lose stale decay data, including chain offset " + offset);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void colonyPermissionsProtectTerrain(GameTestHelper helper) {
        if (net.neoforged.fml.ModList.get().isLoaded("minecolonies")) {
            com.ahmetakif.safepaths.compat.MineColoniesProtectionGameTests.checkPermissions(helper);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void pathCreatorSurvivesSaveLoad(GameTestHelper helper) {
        PathMemorySavedData memory = new PathMemorySavedData();
        PathMemorySavedData.PathData path = new PathMemorySavedData.PathData(Blocks.DIRT.defaultBlockState(), Blocks.DIRT_PATH, 0);
        path.creatorId = UUID.randomUUID();
        path.colonyId = 42;
        memory.putPath(FLOOR, path);
        PathMemorySavedData loaded = PathMemorySavedData.load(
                memory.save(new CompoundTag(), helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        helper.assertTrue(path.creatorId.equals(loaded.getPath(FLOOR).creatorId) && loaded.getPath(FLOOR).colonyId == 42,
                "Permission context must survive restarts");
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void maintenanceSkipsUnloadedPathPositions(GameTestHelper helper) {
        PathMemorySavedData memory = new PathMemorySavedData();
        BlockPos loaded = helper.absolutePos(FLOOR);
        BlockPos unloaded = loaded.offset(4096, 0, 4096);
        helper.assertTrue(!helper.getLevel().isLoaded(unloaded), "The distant chunk must start unloaded");
        memory.putPath(loaded, new PathMemorySavedData.PathData(Blocks.DIRT.defaultBlockState(), Blocks.DIRT_PATH, 0));
        memory.putPath(unloaded, new PathMemorySavedData.PathData(Blocks.SAND.defaultBlockState(), Blocks.DIRT_PATH, 0));
        AtomicInteger visited = new AtomicInteger();
        memory.removeLoadedPathsIf(helper.getLevel(), entry -> {
            visited.incrementAndGet();
            return true;
        });
        helper.assertTrue(visited.get() == 1, "Maintenance must only visit loaded path positions");
        helper.assertTrue(memory.getPath(loaded) == null && memory.getPath(unloaded) != null,
                "Unloaded paths must retain their data");
        helper.assertTrue(!helper.getLevel().isLoaded(unloaded), "Maintenance must not force-load chunks");
        memory.clearAt(unloaded);
        memory.removeLoadedPathsIf(helper.getLevel(), entry -> {
            helper.fail("Cleared records must not remain in the chunk index");
            return false;
        });
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void selfMappingProtectsPreviouslyTrackedRoads(GameTestHelper helper) {
        helper.setBlock(DECAY_ROAD.below(), Blocks.STONE);
        helper.setBlock(DECAY_ROAD, Blocks.GRAVEL);
        PathMemorySavedData memory = PathMemorySavedData.get(helper.getLevel());
        memory.putPath(helper.absolutePos(DECAY_ROAD), new PathMemorySavedData.PathData(
                Blocks.SAND.defaultBlockState(), Blocks.GRAVEL,
                helper.getLevel().getGameTime() - PathConfig.DECAY_TIME.get() - 100));
        helper.runAfterDelay(60, () -> {
            helper.assertBlockPresent(Blocks.GRAVEL, DECAY_ROAD);
            helper.assertTrue(memory.getPath(helper.absolutePos(DECAY_ROAD)) == null,
                    "Self-mapping must remove pre-existing decay tracking without changing the road");
            helper.succeed();
        });
    }

    @GameTest(template = "platform")
    public static void stickyPistonClearsPulledSourceAndDestination(GameTestHelper helper) {
        BlockPos piston = new BlockPos(0, 2, 2);
        helper.setBlock(piston, Blocks.STICKY_PISTON.defaultBlockState()
                .setValue(PistonBaseBlock.FACING, Direction.EAST).setValue(PistonBaseBlock.EXTENDED, true));
        helper.setBlock(piston.east(), Blocks.AIR);
        helper.setBlock(piston.east(2), Blocks.DIRT_PATH);
        PathMemorySavedData memory = PathMemorySavedData.get(helper.getLevel());
        for (int offset = 1; offset <= 2; offset++) {
            memory.putPath(helper.absolutePos(piston.east(offset)), new PathMemorySavedData.PathData(
                    Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT_PATH, helper.getLevel().getGameTime()));
        }
        PathEvents.onPiston(new PistonEvent.Pre(helper.getLevel(), helper.absolutePos(piston),
                Direction.EAST, PistonEvent.PistonMoveType.RETRACT));
        helper.assertTrue(memory.getPath(helper.absolutePos(piston.east())) == null
                        && memory.getPath(helper.absolutePos(piston.east(2))) == null,
                "Sticky piston pulls must clear both the source and destination");
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void loadedPathIndexSurvivesSaveLoad(GameTestHelper helper) {
        PathMemorySavedData memory = new PathMemorySavedData();
        BlockPos pos = helper.absolutePos(FLOOR);
        memory.putPath(pos, new PathMemorySavedData.PathData(Blocks.DIRT.defaultBlockState(), Blocks.DIRT_PATH, 0));
        PathMemorySavedData loaded = PathMemorySavedData.load(
                memory.save(new CompoundTag(), helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        AtomicInteger visited = new AtomicInteger();
        loaded.removeLoadedPathsIf(helper.getLevel(), entry -> {
            visited.incrementAndGet();
            return true;
        });
        helper.assertTrue(visited.get() == 1 && loaded.getPath(pos) == null,
                "Loaded path maintenance must still work after a world restart");
        helper.succeed();
    }
}
