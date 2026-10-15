package com.ahmetakif.safepaths.client.gui;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SafePathsConfigScreen extends Screen {
    private final Screen parentScreen;

    private EditBox requiredPassesBox;
    private EditBox constructionTimeBox;
    private EditBox decayTimeBox;

    public SafePathsConfigScreen(Screen parentScreen) {
        super(Component.translatable("safepaths.configuration.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 46;
        int rowSpacing = 38;
        int boxWidth = 200;
        int boxHeight = 18;
        int boxX = centerX - boxWidth / 2;

        // 1. Required Passes
        int currentY = startY;
        this.requiredPassesBox = new EditBox(this.font, boxX, currentY + 12, boxWidth, boxHeight, Component.translatable("config.safepaths.requiredPasses"));
        this.requiredPassesBox.setMaxLength(6);
        this.requiredPassesBox.setValue(String.valueOf(PathConfig.REQUIRED_PASSES.get()));
        this.requiredPassesBox.setTooltip(Tooltip.create(Component.translatable("config.safepaths.requiredPasses.tooltip")));
        this.addRenderableWidget(this.requiredPassesBox);

        // 2. Construction Time
        currentY += rowSpacing;
        this.constructionTimeBox = new EditBox(this.font, boxX, currentY + 12, boxWidth, boxHeight, Component.translatable("config.safepaths.constructionTime"));
        this.constructionTimeBox.setMaxLength(8);
        this.constructionTimeBox.setValue(String.valueOf(PathConfig.CONSTRUCTION_TIME.get()));
        this.constructionTimeBox.setTooltip(Tooltip.create(Component.translatable("config.safepaths.constructionTime.tooltip")));
        this.addRenderableWidget(this.constructionTimeBox);

        // 3. Decay Time
        currentY += rowSpacing;
        this.decayTimeBox = new EditBox(this.font, boxX, currentY + 12, boxWidth, boxHeight, Component.translatable("config.safepaths.decayTime"));
        this.decayTimeBox.setMaxLength(8);
        this.decayTimeBox.setValue(String.valueOf(PathConfig.DECAY_TIME.get()));
        this.decayTimeBox.setTooltip(Tooltip.create(Component.translatable("config.safepaths.decayTime.tooltip")));
        this.addRenderableWidget(this.decayTimeBox);

        // 4. Configure Conversions button
        currentY += rowSpacing + 6;
        this.addRenderableWidget(Button.builder(
                Component.translatable("safepaths.gui.conversions.configure"),
                b -> this.minecraft.setScreen(new CustomConversionsScreen(this))
        ).bounds(boxX, currentY, boxWidth, 20).build());

        // Bottom Controls (Restore Defaults, Done, Cancel)
        int bottomY = this.height - 28;
        int btnWidth = 96;

        this.addRenderableWidget(Button.builder(
                Component.translatable("safepaths.gui.restore_defaults"),
                b -> restoreDefaults()
        ).bounds(centerX - btnWidth - btnWidth / 2 - 8, bottomY, btnWidth, 20).build());

        this.addRenderableWidget(Button.builder(
                CommonComponents.GUI_DONE,
                b -> saveAndClose()
        ).bounds(centerX - btnWidth / 2, bottomY, btnWidth, 20).build());

        this.addRenderableWidget(Button.builder(
                CommonComponents.GUI_CANCEL,
                b -> this.minecraft.setScreen(this.parentScreen)
        ).bounds(centerX + btnWidth / 2 + 8, bottomY, btnWidth, 20).build());
    }

    private void restoreDefaults() {
        this.requiredPassesBox.setValue("20");
        this.constructionTimeBox.setValue("24000");
        this.decayTimeBox.setValue("72000");
    }

    private void saveAndClose() {
        try {
            int passes = Integer.parseInt(this.requiredPassesBox.getValue().trim());
            PathConfig.REQUIRED_PASSES.set(Mth.clamp(passes, 1, 1000));
        } catch (NumberFormatException ignored) {}

        try {
            int constructTime = Integer.parseInt(this.constructionTimeBox.getValue().trim());
            PathConfig.CONSTRUCTION_TIME.set(Mth.clamp(constructTime, 1000, 72000));
        } catch (NumberFormatException ignored) {}

        try {
            int decay = Integer.parseInt(this.decayTimeBox.getValue().trim());
            PathConfig.DECAY_TIME.set(Mth.clamp(decay, 1000, 240000));
        } catch (NumberFormatException ignored) {}

        PathConfig.SPEC.save();
        this.minecraft.setScreen(this.parentScreen);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        // Header Title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 16, 0xFFFFFF);

        int centerX = this.width / 2;
        int startY = 46;
        int rowSpacing = 38;

        // Field Labels
        graphics.drawCenteredString(this.font, Component.translatable("config.safepaths.requiredPasses"), centerX, startY, 0xDDDDDD);
        graphics.drawCenteredString(this.font, Component.translatable("config.safepaths.constructionTime"), centerX, startY + rowSpacing, 0xDDDDDD);
        graphics.drawCenteredString(this.font, Component.translatable("config.safepaths.decayTime"), centerX, startY + rowSpacing * 2, 0xDDDDDD);
    }
}
