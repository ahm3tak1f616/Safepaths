package com.ahmetakif.safepaths.client;

import com.ahmetakif.safepaths.client.gui.CustomConversionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
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
            return new ConfigurationScreen(modContainer, parent, (context, key, element) -> {
                if ("enableSpeedBoost".equals(key) || "speedMultiplier".equals(key)) {
                    return null;
                }
                if ("customConversions".equals(key)) {
                    Button.Builder buttonBuilder = Button.builder(
                            Component.translatable("safepaths.gui.conversions.configure"),
                            b -> Minecraft.getInstance().setScreen(new CustomConversionsScreen(context.parent()))
                    ).width(150);

                    Component tooltip = element.tooltip();
                    if (tooltip != null) {
                        buttonBuilder.tooltip(Tooltip.create(tooltip));
                    }

                    return new ConfigurationScreen.ConfigurationSectionScreen.Element(
                            element.name(),
                            element.tooltip(),
                            buttonBuilder.build(),
                            false
                    );
                }
                return element;
            });
        });
    }
}
