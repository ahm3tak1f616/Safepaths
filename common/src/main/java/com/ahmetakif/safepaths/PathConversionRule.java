package com.ahmetakif.safepaths;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record PathConversionRule(ResourceLocation source, ResourceLocation target, Double speed) {
    public static PathConversionRule parse(String raw) {
        if (raw == null) return null;
        String[] speedParts = raw.split("@", -1);
        if (speedParts.length > 2) return null;
        String[] blocks = speedParts[0].split("->", -1);
        if (blocks.length != 2) return null;
        ResourceLocation source = ResourceLocation.tryParse(blocks[0].trim());
        ResourceLocation target = ResourceLocation.tryParse(blocks[1].trim());
        if (source == null || target == null) return null;
        Double speed = null;
        if (speedParts.length == 2) {
            try {
                speed = Double.parseDouble(speedParts[1].trim().replace(',', '.'));
            } catch (NumberFormatException ignored) {
                return null;
            }
            if (!Double.isFinite(speed) || speed < 0.0 || speed > 5.0) return null;
        }
        return new PathConversionRule(source, target, speed);
    }

    public String problem() {
        if (!BuiltInRegistries.BLOCK.containsKey(source) || !BuiltInRegistries.BLOCK.containsKey(target)) {
            return "safepaths.gui.rule.missing";
        }
        BlockState sourceState = BuiltInRegistries.BLOCK.get(source).defaultBlockState();
        BlockState targetState = BuiltInRegistries.BLOCK.get(target).defaultBlockState();
        if (!isSafeRoad(targetState)) return "safepaths.gui.rule.unsafe_target";
        if (sourceState.isAir() || sourceState.hasBlockEntity()
                || (!source.equals(target) && isProtectedSource(sourceState))) {
            return "safepaths.gui.rule.protected_source";
        }
        return null;
    }

    public static boolean isSafeRoad(BlockState state) {
        return !state.isAir() && !state.liquid() && state.blocksMotion() && !state.hasBlockEntity();
    }

    public static boolean isProtectedSource(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return state.hasBlockEntity() || state.is(Blocks.FARMLAND)
                || state.is(PathCreationEvent.CANNOT_BECOME_PATH)
                || id.getPath().contains("farmland") || id.getNamespace().equals("minecolonies");
    }

    public String serialize() {
        return source + " -> " + target + (speed == null ? "" : " @ " + speed);
    }
}
