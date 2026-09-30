package com.ahmetakif.safepaths.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class PathConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<Integer> REQUIRED_PASSES;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SPEED_BOOST;
    public static final ModConfigSpec.ConfigValue<Double> SPEED_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Integer> DECAY_TIME;
    public static final ModConfigSpec.ConfigValue<Integer> CONSTRUCTION_TIME;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_CONVERSIONS;

    public static final List<String> DEFAULT_CONVERSIONS = List.of(
            "minecraft:grass_block -> minecraft:dirt_path",
            "minecraft:dirt -> minecraft:dirt_path",
            "minecraft:sand -> minecraft:dirt_path",
            "minecraft:gravel -> minecraft:gravel",
            "biomeswevegone:lush_grass_block -> biomeswevegone:lush_dirt_path",
            "biomeswevegone:lush_dirt -> biomeswevegone:lush_dirt_path",
            "biomeswevegone:sandy_dirt -> biomeswevegone:sandy_dirt_path",
            "biomesoplenty:origin_grass_block -> minecraft:dirt_path",
            "regions_unexplored:peat_grass_block -> regions_unexplored:peat_dirt_path",
            "regions_unexplored:peat_dirt -> regions_unexplored:peat_dirt_path",
            "regions_unexplored:silt_grass_block -> regions_unexplored:silt_dirt_path",
            "regions_unexplored:silt_dirt -> regions_unexplored:silt_dirt_path",
            "regions_unexplored:chalk_grass_block -> regions_unexplored:chalk_dirt_path",
            "regions_unexplored:chalk_dirt -> regions_unexplored:chalk_dirt_path"
    );

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("Path Settings");

        CONSTRUCTION_TIME = builder
                .comment("Time limit to perform required steps before the progress resets. (24,000 ticks = 1 Minecraft day).")
                .translation("config.safepaths.constructionTime")
                .defineInRange("constructionTime", 24000, 1000, 72000);

        REQUIRED_PASSES = builder
                .comment("Number of steps to create a path (e.g., 20)")
                .translation("config.safepaths.requiredPasses")
                .defineInRange("requiredPasses", 20, 1, 1000);

        DECAY_TIME = builder
                .comment("Time in ticks for an unused path to revert (72,000 ticks = 3 Minecraft days).")
                .translation("config.safepaths.decayTime")
                .defineInRange("decayTime", 72000, 1000, 240000);

        ENABLE_SPEED_BOOST = builder
                .comment("Enable or disable movement speed boost on paths.")
                .translation("config.safepaths.enableSpeedBoost")
                .define("enableSpeedBoost", true);

        SPEED_MULTIPLIER = builder
                .comment("Movement speed boost percentage on paths (e.g., 0.2 = +20% speed).")
                .translation("config.safepaths.speedMultiplier")
                .defineInRange("speedMultiplier", 0.2, 0.0, 5.0);

        CUSTOM_CONVERSIONS = builder
                .comment("Custom block conversions in format: source_block -> target_block")
                .translation("config.safepaths.customConversions")
                .defineListAllowEmpty(List.of("customConversions"), () -> DEFAULT_CONVERSIONS, o -> o instanceof String s && s.contains("->"));

        builder.pop();
        SPEC = builder.build();
    }
}
