package com.ahmetakif.safepaths;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

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

    @GameTest(template = "platform", timeoutTicks = 100)
    public static void walkingFormsDirtPath(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.GRASS_BLOCK);
        helper.setBlock(ABOVE, Blocks.AIR);

        ServerLevel level = helper.getLevel();
        BlockPos floorAbs = helper.absolutePos(FLOOR);
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        memory.putTrample(floorAbs, new PathMemorySavedData.TrampleData(
                com.ahmetakif.safepaths.config.PathConfig.REQUIRED_PASSES.get() - 1,
                level.getGameTime()
        ));

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.moveTo(floorAbs.getX() + 0.5, floorAbs.getY() + 1.0, floorAbs.getZ() + 0.5, 0.0F, 0.0F);
        player.setOnGround(true);

        helper.assertTrue(
                player.getOnPos().equals(floorAbs),
                "Mock player should stand on grass, got " + player.getOnPos()
        );

        NeoForge.EVENT_BUS.post(new EntityTickEvent.Post(player));

        helper.assertBlockPresent(Blocks.DIRT_PATH, FLOOR);
        helper.assertTrue(memory.getPath(floorAbs) != null, "Formed path should be tracked in memory");
        helper.succeed();
    }

    @GameTest(template = "platform", timeoutTicks = 100)
    public static void farmlandIsNotTrampledIntoPath(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.FARMLAND);
        helper.setBlock(ABOVE, Blocks.AIR);

        ServerLevel level = helper.getLevel();
        BlockPos floorAbs = helper.absolutePos(FLOOR);
        PathMemorySavedData memory = PathMemorySavedData.get(level);
        memory.putTrample(floorAbs, new PathMemorySavedData.TrampleData(
                com.ahmetakif.safepaths.config.PathConfig.REQUIRED_PASSES.get() - 1,
                level.getGameTime()
        ));

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.moveTo(floorAbs.getX() + 0.5, floorAbs.getY() + 1.0, floorAbs.getZ() + 0.5, 0.0F, 0.0F);
        player.setOnGround(true);

        NeoForge.EVENT_BUS.post(new EntityTickEvent.Post(player));

        helper.assertBlockPresent(Blocks.FARMLAND, FLOOR);
        helper.assertTrue(memory.getPath(floorAbs) == null, "Farmland must not get path memory");
        helper.succeed();
    }
}
