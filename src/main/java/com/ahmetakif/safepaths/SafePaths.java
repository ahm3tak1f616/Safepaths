package com.ahmetakif.safepaths;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("safepaths")
public class SafePaths {
    public SafePaths() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PathConfig.SPEC);
    }
}
