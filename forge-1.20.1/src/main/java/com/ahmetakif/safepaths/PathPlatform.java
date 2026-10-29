package com.ahmetakif.safepaths;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.fml.ModList;
import java.util.UUID;

final class PathPlatform {
    private PathPlatform() {}
    private static final UUID SPEED_ID = UUID.fromString("c8b7f8e1-5e72-4d1a-8c3b-7f1a9e8d2c4e");

    static ResourceLocation id(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    static boolean isModLoaded(String id) { return ModList.get().isLoaded(id); }
    static AttributeModifier speedModifier(AttributeInstance attribute) { return attribute.getModifier(SPEED_ID); }
    static void removeSpeedModifier(AttributeInstance attribute) { attribute.removeModifier(SPEED_ID); }
    static double amount(AttributeModifier modifier) { return modifier.getAmount(); }
    static void addSpeedModifier(AttributeInstance attribute, double multiplier) {
        attribute.addTransientModifier(new AttributeModifier(SPEED_ID, "safepaths:path_speed", multiplier,
                AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
