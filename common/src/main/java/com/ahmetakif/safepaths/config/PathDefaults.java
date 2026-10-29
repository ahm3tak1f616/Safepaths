package com.ahmetakif.safepaths.config;

import java.util.List;

public final class PathDefaults {
    private PathDefaults() {}

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
}
