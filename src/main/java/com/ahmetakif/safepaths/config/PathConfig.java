package com.ahmetakif.safepaths.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class PathConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<Integer> REQUIRED_PASSES;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SPEED_BOOST;
    public static final ModConfigSpec.ConfigValue<Double> SPEED_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Integer> DECAY_TIME;
    public static final ModConfigSpec.ConfigValue<Integer> CONSTRUCTION_TIME;

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

        ENABLE_SPEED_BOOST = builder
                .comment("Enable speed boost on paths")
                .translation("config.safepaths.enableSpeedBoost")
                .define("enableSpeedBoost", true);

        SPEED_MULTIPLIER = builder
                .comment("Movement speed multiplier added on paths (ADD_MULTIPLIED_TOTAL). 0.2 matches Speed I.")
                .translation("config.safepaths.speedMultiplier")
                .defineInRange("speedMultiplier", 0.2, 0.01, 1.0);

        DECAY_TIME = builder
                .comment("Ticks until path decays (24000 ticks = 1 in-game day).")
                .translation("config.safepaths.decayTime")
                .defineInRange("decayTime", 72000, 1000, 144000);

        builder.pop();
        SPEC = builder.build();
    }
}
