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

    private static Player stepOn(GameTestHelper helper, BlockPos pos) {
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        BlockPos absolute = helper.absolutePos(pos);
        player.setPos(absolute.getX() + 0.5, absolute.getY() + 1, absolute.getZ() + 0.5);
        player.setOnGround(true);
        player.mainSupportingBlockPos = Optional.of(absolute);
        PathEvents.onEntityTick(new EntityTickEvent.Post(player));
        return player;
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
    @GameTest(template = "platform")
    public static void conversionSpeedSyntaxIsBackwardCompatible(GameTestHelper helper) {
        helper.assertTrue(PathConversionRule.parse("minecraft:dirt -> minecraft:dirt_path").speed() == null,
                "Existing rules must inherit the global bonus");
        PathConversionRule rule = PathConversionRule.parse("minecraft:dirt -> minecraft:dirt_path @ 0,125");
        helper.assertTrue(rule != null && rule.speed() == 0.125
                && PathConversionRule.parse(rule.serialize()).equals(rule), "Speed rules must round-trip without precision loss");
        for (String speed : List.of("NaN", "Infinity", "-0.1", "5.01", "", "0.2 @ 0.3")) {
            helper.assertTrue(PathConversionRule.parse("minecraft:dirt -> minecraft:dirt_path @ " + speed) == null,
                    "Invalid speed must be rejected: " + speed);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void conversionValidationExplainsProtectedBlocks(GameTestHelper helper) {
        helper.assertTrue("safepaths.gui.rule.unsafe_target".equals(PathConversionRule.parse("minecraft:dirt -> minecraft:chest").problem()),
                "Block-entity targets must have an explicit validation reason");
        helper.assertTrue("safepaths.gui.rule.protected_source".equals(PathConversionRule.parse("minecraft:chest -> minecraft:stone").problem()),
                "Inventories must never become paths");
        helper.assertTrue(PathConversionRule.parse("minecraft:farmland -> minecraft:dirt_path").problem() != null,
                "Farmland must stay protected");
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void missingOptionalRulesRemainConfigured(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        try {
            String missing = "safepaths_absent:soil -> safepaths_absent:road @ 0.3";
            PathConfig.CUSTOM_CONVERSIONS.set(List.of(missing, "minecraft:stone -> minecraft:stone @ 0.4"));
            helper.assertTrue("safepaths.gui.rule.missing".equals(PathConversionRule.parse(missing).problem()),
                    "Missing optional blocks must be reported as inactive");
            helper.assertTrue(PathCreationEvent.isPathBlock(Blocks.STONE.defaultBlockState()), "Valid rules must still load");
            helper.assertTrue(PathConfig.CUSTOM_CONVERSIONS.get().contains(missing), "Cache refresh must not delete missing rules");
        } finally {
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void sameRoadKeepsSourceSpecificSpeeds(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        boolean enabled = PathConfig.ENABLE_SPEED_BOOST.get();
        double global = PathConfig.SPEED_MULTIPLIER.get();
        ServerLevel level = helper.getLevel();
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        BlockPos pos = helper.absolutePos(DECAY_ROAD);
        try {
            PathConfig.ENABLE_SPEED_BOOST.set(true);
            PathConfig.SPEED_MULTIPLIER.set(0.0);
            PathConfig.CUSTOM_CONVERSIONS.set(List.of("minecraft:dirt -> minecraft:stone @ 0.125", "minecraft:sand -> minecraft:stone @ 0.4"));
            helper.setBlock(DECAY_ROAD, Blocks.STONE);
            memory.putPath(pos, new PathMemorySavedData.PathData(Blocks.DIRT.defaultBlockState(), Blocks.STONE, level.getGameTime()));
            helper.assertTrue(PathCreationEvent.speedMultiplier(level, pos, level.getBlockState(pos)) == 0.125, "Dirt conversion must keep its bonus even with a zero global bonus");
            memory.putPath(pos, new PathMemorySavedData.PathData(Blocks.SAND.defaultBlockState(), Blocks.STONE, level.getGameTime()));
            helper.assertTrue(PathCreationEvent.speedMultiplier(level, pos, level.getBlockState(pos)) == 0.4, "Sand conversion must keep its distinct bonus");
            memory.clearAt(pos);
            helper.assertTrue(PathCreationEvent.speedMultiplier(level, pos, level.getBlockState(pos)) == 0.125, "Untracked roads must use the first matching target rule");
            PathConfig.CUSTOM_CONVERSIONS.set(List.of("minecraft:dirt -> minecraft:stone", "minecraft:sand -> minecraft:stone @ 0.4"));
            PathConfig.SPEED_MULTIPLIER.set(0.2);
            helper.assertTrue(PathCreationEvent.speedMultiplier(level, pos, level.getBlockState(pos)) == 0.2, "Rules without an override must inherit the global value");
        } finally {
            memory.clearAt(pos);
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
            PathConfig.ENABLE_SPEED_BOOST.set(enabled);
            PathConfig.SPEED_MULTIPLIER.set(global);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void permanentRoadSpeedOverridesIncomingRules(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        boolean enabled = PathConfig.ENABLE_SPEED_BOOST.get();
        BlockPos pos = helper.absolutePos(DECAY_ROAD);
        PathMemorySavedData memory = PathMemorySavedData.get(helper.getLevel());
        try {
            PathConfig.ENABLE_SPEED_BOOST.set(true);
            PathConfig.CUSTOM_CONVERSIONS.set(List.of("minecraft:dirt -> minecraft:stone @ 0.4", "minecraft:stone -> minecraft:stone @ 0.1"));
            helper.setBlock(DECAY_ROAD, Blocks.STONE);
            memory.putPath(pos, new PathMemorySavedData.PathData(Blocks.DIRT.defaultBlockState(), Blocks.STONE, helper.getLevel().getGameTime()));
            helper.assertTrue(PathCreationEvent.speedMultiplier(helper.getLevel(), pos, Blocks.STONE.defaultBlockState()) == 0.1,
                    "Explicit permanent-road speed must win for that material");
        } finally {
            memory.clearAt(pos);
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
            PathConfig.ENABLE_SPEED_BOOST.set(enabled);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void zeroRoadSpeedRemovesPreviousBonusImmediately(GameTestHelper helper) {
        List<? extends String> previous = PathConfig.CUSTOM_CONVERSIONS.get();
        boolean enabled = PathConfig.ENABLE_SPEED_BOOST.get();
        try {
            PathConfig.ENABLE_SPEED_BOOST.set(true);
            PathConfig.CUSTOM_CONVERSIONS.set(List.of("minecraft:stone -> minecraft:stone @ 0.125", "minecraft:cobblestone -> minecraft:cobblestone @ 0"));
            helper.setBlock(DECAY_ROAD, Blocks.STONE);
            Player player = stepOn(helper, DECAY_ROAD);
            var attribute = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
            helper.assertTrue(PathPlatform.speedModifier(attribute) != null && PathPlatform.amount(PathPlatform.speedModifier(attribute)) == 0.125,
                    "Walking on a configured road must apply the exact bonus");
            helper.setBlock(DECAY_ROAD, Blocks.COBBLESTONE);
            PathCreationEvent.tickEntity(player);
            helper.assertTrue(PathPlatform.speedModifier(attribute) == null, "A zero-bonus road must replace the previous road contact immediately");
            helper.setBlock(DECAY_ROAD, Blocks.STONE);
            PathCreationEvent.tickEntity(player);
            PathConfig.ENABLE_SPEED_BOOST.set(false);
            PathCreationEvent.tickEntity(player);
            helper.assertTrue(PathPlatform.speedModifier(attribute) == null, "The master toggle must also disable per-rule bonuses");
        } finally {
            PathConfig.CUSTOM_CONVERSIONS.set(previous);
            PathConfig.ENABLE_SPEED_BOOST.set(enabled);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void creationTogglePreventsNewPasses(GameTestHelper helper) {
        boolean creation = PathConfig.ENABLE_PATH_CREATION.get();
        PathMemorySavedData memory = PathMemorySavedData.get(helper.getLevel());
        BlockPos pos = helper.absolutePos(DECAY_ROAD);
        try {
            helper.setBlock(DECAY_ROAD.below(), Blocks.STONE);
            helper.setBlock(DECAY_ROAD, Blocks.DIRT);
            memory.clearAt(pos);
            PathConfig.ENABLE_PATH_CREATION.set(false);
            stepOn(helper, DECAY_ROAD);
            helper.assertTrue(!memory.hasTrample(pos), "Disabled creation must not add construction progress");
            helper.assertBlockPresent(Blocks.DIRT, DECAY_ROAD);
        } finally {
            PathConfig.ENABLE_PATH_CREATION.set(creation);
            memory.clearAt(pos);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void sneakingSkipsConstructionButRefreshesExistingRoads(GameTestHelper helper) {
        boolean sneak = PathConfig.SNEAK_PREVENTS_PATH_CREATION.get();
        boolean creation = PathConfig.ENABLE_PATH_CREATION.get();
        int required = PathConfig.REQUIRED_PASSES.get();
        PathMemorySavedData memory = PathMemorySavedData.get(helper.getLevel());
        BlockPos pos = helper.absolutePos(DECAY_ROAD);
        try {
            PathConfig.SNEAK_PREVENTS_PATH_CREATION.set(true);
            PathConfig.ENABLE_PATH_CREATION.set(true);
            PathConfig.REQUIRED_PASSES.set(20);
            helper.setBlock(DECAY_ROAD.below(), Blocks.STONE);
            helper.setBlock(DECAY_ROAD, Blocks.DIRT);
            memory.clearAt(pos);
            Player player = stepOn(helper, DECAY_ROAD);
            memory.clearAt(pos);
            player.setShiftKeyDown(true);
            BlockPos neighbor = pos.east();
            player.setPos(neighbor.getX() + 0.5, neighbor.getY() + 1, neighbor.getZ() + 0.5);
            player.mainSupportingBlockPos = Optional.of(neighbor);
            PathCreationEvent.tickEntity(player);
            player.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            player.mainSupportingBlockPos = Optional.of(pos);
            PathCreationEvent.tickEntity(player);
            helper.assertTrue(!memory.hasTrample(pos), "Sneaking must not add passes after moving back to eligible terrain");
            helper.setBlock(DECAY_ROAD, Blocks.DIRT_PATH);
            PathMemorySavedData.PathData path = new PathMemorySavedData.PathData(Blocks.DIRT.defaultBlockState(), Blocks.DIRT_PATH, helper.getLevel().getGameTime() - 40);
            memory.putPath(pos, path);
            PathConfig.ENABLE_PATH_CREATION.set(false);
            PathCreationEvent.tickEntity(player);
            helper.assertTrue(path.lastTime == helper.getLevel().getGameTime(), "Sneaking with creation disabled must still refresh an existing road");
            helper.setBlock(DECAY_ROAD, Blocks.DIRT);
            memory.clearAt(pos);
            PathConfig.ENABLE_PATH_CREATION.set(true);
            PathConfig.SNEAK_PREVENTS_PATH_CREATION.set(false);
            player.mainSupportingBlockPos = Optional.of(neighbor);
            PathCreationEvent.tickEntity(player);
            player.mainSupportingBlockPos = Optional.of(pos);
            PathCreationEvent.tickEntity(player);
            helper.assertTrue(memory.hasTrample(pos), "The sneak option must be configurable independently");
        } finally {
            PathConfig.SNEAK_PREVENTS_PATH_CREATION.set(sneak);
            PathConfig.ENABLE_PATH_CREATION.set(creation);
            PathConfig.REQUIRED_PASSES.set(required);
            memory.clearAt(pos);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void mudAndMossActuallyFormPaths(GameTestHelper helper) {
        int required = PathConfig.REQUIRED_PASSES.get();
        boolean creation = PathConfig.ENABLE_PATH_CREATION.get();
        boolean sneak = PathConfig.SNEAK_PREVENTS_PATH_CREATION.get();
        BlockPos pos = helper.absolutePos(DECAY_ROAD);
        try {
            PathConfig.REQUIRED_PASSES.set(1);
            PathConfig.ENABLE_PATH_CREATION.set(true);
            PathConfig.SNEAK_PREVENTS_PATH_CREATION.set(false);
            helper.setBlock(DECAY_ROAD.below(), Blocks.STONE);
            for (Block block : List.of(Blocks.MUD, Blocks.MOSS_BLOCK)) {
                helper.setBlock(DECAY_ROAD, block);
                stepOn(helper, DECAY_ROAD);
                helper.assertBlockPresent(Blocks.DIRT_PATH, DECAY_ROAD);
                helper.assertTrue(PathMemorySavedData.get(helper.getLevel()).getPath(pos).originalState.is(block), "Original mud or moss must be retained for decay");
            }
        } finally {
            PathConfig.REQUIRED_PASSES.set(required);
            PathConfig.ENABLE_PATH_CREATION.set(creation);
            PathConfig.SNEAK_PREVENTS_PATH_CREATION.set(sneak);
            PathMemorySavedData.get(helper.getLevel()).clearAt(pos);
        }
        helper.succeed();
    }

    @GameTest(template = "platform")
    public static void decayToggleRetainsRecordsAndResumesExpiredPaths(GameTestHelper helper) {
        helper.succeedWhen(() -> {
            helper.assertTrue(helper.getLevel().getGameTime() % 20 == 0, "Wait for the maintenance tick");
            boolean decay = PathConfig.ENABLE_PATH_DECAY.get();
            try {
                createExpiredPath(helper, Blocks.DIRT, Blocks.STONE);
                PathConfig.ENABLE_PATH_DECAY.set(false);
                PathCreationEvent.tickLevel(helper.getLevel());
                assertDecayDeferred(helper, Blocks.DIRT);
                PathConfig.ENABLE_PATH_DECAY.set(true);
                PathCreationEvent.tickLevel(helper.getLevel());
                helper.assertBlockPresent(Blocks.DIRT, DECAY_ROAD);
                helper.assertTrue(PathMemorySavedData.get(helper.getLevel()).getPath(helper.absolutePos(DECAY_ROAD)) == null,
                        "Re-enabling decay must restore an expired path without losing its original terrain");
            } finally {
                PathConfig.ENABLE_PATH_DECAY.set(decay);
            }
        });
    }

    @GameTest(template = "platform")
    public static void inspectCommandRequiresOperatorAndSkipsUnloadedChunks(GameTestHelper helper) {
        var dispatcher = new com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack>();
        PathCommands.register(dispatcher);
        var source = helper.getLevel().getServer().createCommandSourceStack().withLevel(helper.getLevel());
        helper.assertTrue(!dispatcher.getRoot().getChild("safepaths").canUse(source.withPermission(0)), "Inspect must require operator permission");
        BlockPos pos = helper.absolutePos(DECAY_ROAD);
        try {
            helper.assertTrue(dispatcher.execute("safepaths inspect " + pos.getX() + " " + pos.getY() + " " + pos.getZ(), source.withPermission(2)) == 1,
                    "The registered command must inspect loaded terrain");
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            helper.fail(exception.getMessage());
        }
        BlockPos unloaded = pos.offset(4096, 0, 4096);
        helper.assertTrue(!helper.getLevel().isLoaded(unloaded), "The distant chunk must start unloaded");
        helper.assertTrue(PathCommands.inspect(source, unloaded) == 0 && !helper.getLevel().isLoaded(unloaded),
                "Inspect must never force-load an absent chunk");
        helper.succeed();
    }

}
