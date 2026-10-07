package com.ahmetakif.safepaths;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@SuppressWarnings({"unused", "removal"})
@GameTestHolder("safepaths")
@PrefixGameTestTemplate(false)
public class SafePathsGameTests {
    private static final BlockPos FLOOR = new BlockPos(2, 0, 2);
    private static final BlockPos ABOVE = new BlockPos(2, 1, 2);

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
}
