package com.ahmetakif.safepaths.compat;

import com.ahmetakif.safepaths.PathMemorySavedData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.permissions.IPermissions;
import com.minecolonies.api.colony.permissions.Rank;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import java.lang.reflect.Proxy;
import java.util.UUID;

public final class MineColoniesProtectionGameTests {
    private MineColoniesProtectionGameTests() {}

    private static IColony colony(boolean canBreak, boolean canPlace) {
        IPermissions permissions = (IPermissions) Proxy.newProxyInstance(IPermissions.class.getClassLoader(),
                new Class<?>[]{IPermissions.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getRank" -> new Rank(1, "Resident", false);
                    case "hasPermission" -> args[1] == Action.BREAK_BLOCKS ? canBreak : canPlace;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        return (IColony) Proxy.newProxyInstance(IColony.class.getClassLoader(), new Class<?>[]{IColony.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getPermissions" -> permissions;
                    case "getID" -> 1;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    public static void checkPermissions(GameTestHelper helper) {
        var player = helper.makeMockPlayer();
        helper.assertTrue(MineColoniesProtection.allows(null, player), "Unclaimed terrain should remain pathable");
        helper.assertTrue(!MineColoniesProtection.allows(colony(false, false), player), "Visitors must not change claimed ground");
        helper.assertTrue(!MineColoniesProtection.allows(colony(true, false), player), "Place permission is also required");
        helper.assertTrue(!MineColoniesProtection.allows(colony(false, true), player), "Break permission is also required");
        helper.assertTrue(MineColoniesProtection.allows(colony(true, true), player), "Authorized residents should form paths");

        PathMemorySavedData.PathData path = new PathMemorySavedData.PathData(
                Blocks.DIRT.defaultBlockState(), Blocks.DIRT_PATH, 0);
        helper.assertTrue(!MineColoniesProtection.allowsDecay(colony(true, true), helper.getLevel(), path),
                "Legacy paths without a recorded creator must not modify a claim");
        path.colonyId = 1;
        helper.assertTrue(MineColoniesProtection.allowsDecay(colony(false, false), helper.getLevel(), path),
                "A colony's own recorded citizen paths may decay");
        path.colonyId = 2;
        helper.assertTrue(!MineColoniesProtection.allowsDecay(colony(true, true), helper.getLevel(), path),
                "A new colony owner must not inherit another colony's terrain changes");
        path.creatorId = UUID.randomUUID();
        helper.assertTrue(!MineColoniesProtection.allowsDecay(colony(false, true), helper.getLevel(), path),
                "Revoked creator permission must pause decay");
        helper.assertTrue(MineColoniesProtection.allowsDecay(colony(true, true), helper.getLevel(), path),
                "Offline authorized creators must remain supported");
    }
}
