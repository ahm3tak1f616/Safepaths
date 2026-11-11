package com.ahmetakif.safepaths.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class PathConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> REQUIRED_PASSES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_PATH_CREATION;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_PATH_DECAY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SNEAK_PREVENTS_PATH_CREATION;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_SPEED_BOOST;
    public static final ForgeConfigSpec.ConfigValue<Double> SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Integer> DECAY_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> CONSTRUCTION_TIME;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CUSTOM_CONVERSIONS;

    public static final List<String> DEFAULT_CONVERSIONS = PathDefaults.DEFAULT_CONVERSIONS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Path Settings");

        ENABLE_PATH_CREATION = builder
                .comment("Allow footsteps to create new paths.")
                .translation("config.safepaths.enablePathCreation")
                .define("enablePathCreation", true);

        ENABLE_PATH_DECAY = builder
                .comment("Allow tracked paths to restore their original terrain. Records and elapsed time are retained while disabled; expired paths may revert when enabled again.")
                .translation("config.safepaths.enablePathDecay")
                .define("enablePathDecay", true);

        SNEAK_PREVENTS_PATH_CREATION = builder
                .comment("Sneaking players do not add construction passes. Existing roads still receive traffic.")
                .translation("config.safepaths.sneakPreventsPathCreation")
                .define("sneakPreventsPathCreation", false);

        CONSTRUCTION_TIME = builder
                .comment("Ticks from the first counted pass to reach requiredPasses. Later passes do not extend this window. 24,000 ticks = 1 Minecraft day.")
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
                .comment("Global road speed bonus as a fraction (0.2 = +20%, range 0.0 to 5.0). Rules without a speed inherit this value.")
                .translation("config.safepaths.speedMultiplier")
                .defineInRange("speedMultiplier", 0.2, 0.0, 5.0);

        CUSTOM_CONVERSIONS = builder
                .comment("Custom block conversions: source_block -> target_block, optionally @ speed (0.2 = +20%). Omit speed to use the global bonus.")
                .translation("config.safepaths.customConversions")
                .defineListAllowEmpty(List.of("customConversions"), () -> DEFAULT_CONVERSIONS, o -> o instanceof String s && s.contains("->"));

        builder.pop();
        SPEC = builder.build();
    }
}
