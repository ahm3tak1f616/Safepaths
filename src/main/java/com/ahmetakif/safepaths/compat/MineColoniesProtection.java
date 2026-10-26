package com.ahmetakif.safepaths.compat;

import com.ahmetakif.safepaths.PathMemorySavedData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.entity.citizen.AbstractCivilianEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class MineColoniesProtection {
    private MineColoniesProtection() {}

    public static boolean canTrample(ServerLevel level, BlockPos pos, LivingEntity actor) {
        return allows(IColonyManager.getInstance().getColonyByPosFromWorld(level, pos), actor);
    }

    public static boolean allows(IColony colony, LivingEntity actor) {
        if (colony == null) {
            return true;
        }
        if (actor instanceof Player player) {
            return colony.getPermissions().hasPermission(player, Action.BREAK_BLOCKS)
                    && colony.getPermissions().hasPermission(player, Action.PLACE_BLOCKS);
        }
        return actor instanceof AbstractCivilianEntity civilian && civilian.getCivilianData() != null
                && civilian.getCivilianData().getColony() != null
                && civilian.getCivilianData().getColony().getID() == colony.getID();
    }

    public static int colonyId(ServerLevel level, BlockPos pos) {
        IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, pos);
        return colony == null ? 0 : colony.getID();
    }

    public static boolean canDecay(ServerLevel level, BlockPos pos, PathMemorySavedData.PathData data) {
        return allowsDecay(IColonyManager.getInstance().getColonyByPosFromWorld(level, pos), level, data);
    }

    public static boolean allowsDecay(IColony colony, ServerLevel level, PathMemorySavedData.PathData data) {
        if (colony == null) {
            return true;
        }
        if (data.creatorId != null) {
            Player player = level.getServer().getPlayerList().getPlayer(data.creatorId);
            if (player != null) {
                return allows(colony, player);
            }
            var rank = colony.getPermissions().getRank(data.creatorId);
            return colony.getPermissions().hasPermission(rank, Action.BREAK_BLOCKS)
                    && colony.getPermissions().hasPermission(rank, Action.PLACE_BLOCKS);
        }
        return data.colonyId != 0 && data.colonyId == colony.getID();
    }
}
