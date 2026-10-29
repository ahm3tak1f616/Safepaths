package com.ahmetakif.safepaths;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.fml.ModList;

final class PathPlatform {
    private PathPlatform() {}
    private static final ResourceLocation SPEED_ID = id("safepaths", "path_speed");

    static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    static boolean isModLoaded(String id) { return ModList.get().isLoaded(id); }
    static AttributeModifier speedModifier(AttributeInstance attribute) { return attribute.getModifier(SPEED_ID); }
    static void removeSpeedModifier(AttributeInstance attribute) { attribute.removeModifier(SPEED_ID); }
    static double amount(AttributeModifier modifier) { return modifier.amount(); }
    static void addSpeedModifier(AttributeInstance attribute, double multiplier) {
        attribute.addTransientModifier(new AttributeModifier(SPEED_ID, multiplier,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
}
