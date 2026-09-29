package com.ahmetakif.safepaths;

import com.ahmetakif.safepaths.config.PathConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod("safepaths")
public class SafePaths {
    public SafePaths(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, PathConfig.SPEC);
    }
}
