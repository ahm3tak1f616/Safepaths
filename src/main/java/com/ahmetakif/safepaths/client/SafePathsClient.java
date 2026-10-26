package com.ahmetakif.safepaths.client;

import com.ahmetakif.safepaths.client.gui.SafePathsConfigScreen;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "safepaths", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SafePathsClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, screen) -> {
                            if (minecraft.getConnection() != null && !minecraft.hasSingleplayerServer()) {
                                return new AlertScreen(() -> minecraft.setScreen(screen),
                                        Component.translatable("safepaths.configuration.title"),
                                        Component.translatable("safepaths.gui.server_config"));
                            }
                            return new SafePathsConfigScreen(screen);
                        }
                )
        );
    }
}
