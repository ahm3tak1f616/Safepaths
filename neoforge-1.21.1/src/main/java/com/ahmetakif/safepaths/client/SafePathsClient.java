package com.ahmetakif.safepaths.client;

import com.ahmetakif.safepaths.client.gui.SafePathsConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "safepaths", dist = Dist.CLIENT)
public class SafePathsClient {
    public SafePathsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.getConnection() != null && !minecraft.hasSingleplayerServer()) {
                return new AlertScreen(() -> minecraft.setScreen(parent),
                        Component.translatable("safepaths.configuration.title"),
                        Component.translatable("safepaths.gui.server_config"));
            }
            return new SafePathsConfigScreen(parent);
        });
    }
}
