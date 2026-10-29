package com.ahmetakif.safepaths.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class PathConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> REQUIRED_PASSES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_SPEED_BOOST;
    public static final ForgeConfigSpec.ConfigValue<Double> SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Integer> DECAY_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> CONSTRUCTION_TIME;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CUSTOM_CONVERSIONS;

    public static final List<String> DEFAULT_CONVERSIONS = PathDefaults.DEFAULT_CONVERSIONS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
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
