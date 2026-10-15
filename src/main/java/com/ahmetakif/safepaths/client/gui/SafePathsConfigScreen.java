package com.ahmetakif.safepaths.client.gui;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Locale;

public class SafePathsConfigScreen extends Screen {
    private final Screen parentScreen;

    private EditBox requiredPassesBox;
    private EditBox constructionTimeBox;
    private EditBox decayTimeBox;

    private Button speedBoostBtn;
    private EditBox speedMultiplierBox;
    private boolean speedBoostEnabled;

    public SafePathsConfigScreen(Screen parentScreen) {
        super(Component.translatable("safepaths.configuration.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int leftColX = centerX - 155;
        int rightColX = centerX + 5;
        int colWidth = 150;
        int boxHeight = 20;

        this.requiredPassesBox = new EditBox(this.font, leftColX, 42, colWidth, boxHeight, Component.translatable("config.safepaths.requiredPasses"));
        this.requiredPassesBox.setMaxLength(6);
        this.requiredPassesBox.setValue(String.valueOf(PathConfig.REQUIRED_PASSES.get()));
        this.requiredPassesBox.setTooltip(Tooltip.create(Component.translatable("config.safepaths.requiredPasses.tooltip")));
        this.addRenderableWidget(this.requiredPassesBox);

        this.speedBoostEnabled = PathConfig.ENABLE_SPEED_BOOST.get();
        this.speedBoostBtn = Button.builder(getSpeedBoostButtonText(), b -> {
            this.speedBoostEnabled = !this.speedBoostEnabled;
            this.speedBoostBtn.setMessage(getSpeedBoostButtonText());
        }).bounds(rightColX, 42, colWidth, boxHeight)
          .tooltip(Tooltip.create(Component.translatable("config.safepaths.enableSpeedBoost.tooltip")))
          .build();
        this.addRenderableWidget(this.speedBoostBtn);

        this.constructionTimeBox = new EditBox(this.font, leftColX, 84, colWidth, boxHeight, Component.translatable("config.safepaths.constructionTime"));
        this.constructionTimeBox.setMaxLength(8);
        this.constructionTimeBox.setValue(String.valueOf(PathConfig.CONSTRUCTION_TIME.get()));
        this.constructionTimeBox.setTooltip(Tooltip.create(Component.translatable("config.safepaths.constructionTime.tooltip")));
        this.addRenderableWidget(this.constructionTimeBox);

        this.speedMultiplierBox = new EditBox(this.font, rightColX, 84, colWidth, boxHeight, Component.translatable("config.safepaths.speedMultiplier"));
        this.speedMultiplierBox.setMaxLength(6);
        this.speedMultiplierBox.setValue(String.format(Locale.ROOT, "%.2f", PathConfig.SPEED_MULTIPLIER.get()));
        this.speedMultiplierBox.setTooltip(Tooltip.create(Component.translatable("config.safepaths.speedMultiplier.tooltip")));
        this.addRenderableWidget(this.speedMultiplierBox);

        this.decayTimeBox = new EditBox(this.font, leftColX, 126, colWidth, boxHeight, Component.translatable("config.safepaths.decayTime"));
        this.decayTimeBox.setMaxLength(8);
        this.decayTimeBox.setValue(String.valueOf(PathConfig.DECAY_TIME.get()));
        this.decayTimeBox.setTooltip(Tooltip.create(Component.translatable("config.safepaths.decayTime.tooltip")));
        this.addRenderableWidget(this.decayTimeBox);

        this.addRenderableWidget(Button.builder(
                Component.translatable("safepaths.gui.conversions.configure"),
                b -> this.minecraft.setScreen(new CustomConversionsScreen(this))
        ).bounds(rightColX, 126, colWidth, boxHeight)
         .tooltip(Tooltip.create(Component.translatable("config.safepaths.customConversions.tooltip")))
         .build());

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

    private Component getSpeedBoostButtonText() {
        return Component.translatable(this.speedBoostEnabled ? "safepaths.gui.speed_boost.on" : "safepaths.gui.speed_boost.off");
    }

    private void restoreDefaults() {
        this.requiredPassesBox.setValue("20");
        this.constructionTimeBox.setValue("24000");
        this.decayTimeBox.setValue("72000");
        this.speedBoostEnabled = true;
        this.speedBoostBtn.setMessage(getSpeedBoostButtonText());
        this.speedMultiplierBox.setValue("0.20");
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

        PathConfig.ENABLE_SPEED_BOOST.set(this.speedBoostEnabled);

        try {
            String rawSpeed = this.speedMultiplierBox.getValue().trim().replace(',', '.');
            double multiplier = Double.parseDouble(rawSpeed);
            multiplier = Mth.clamp(multiplier, 0.0, 5.0);
            PathConfig.SPEED_MULTIPLIER.set(multiplier);
        } catch (NumberFormatException ignored) {}

        PathConfig.SPEC.save();
        this.minecraft.setScreen(this.parentScreen);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFFF);

        int centerX = this.width / 2;
        int leftColCenter = centerX - 80;
        int rightColCenter = centerX + 80;

        graphics.drawCenteredString(this.font, Component.translatable("config.safepaths.requiredPasses"), leftColCenter, 30, 0xDDDDDD);
        graphics.drawCenteredString(this.font, Component.translatable("config.safepaths.enableSpeedBoost"), rightColCenter, 30, 0xDDDDDD);

        graphics.drawCenteredString(this.font, Component.translatable("config.safepaths.constructionTime"), leftColCenter, 72, 0xDDDDDD);
        graphics.drawCenteredString(this.font, Component.translatable("config.safepaths.speedMultiplier"), rightColCenter, 72, 0xDDDDDD);

        graphics.drawCenteredString(this.font, Component.translatable("config.safepaths.decayTime"), leftColCenter, 114, 0xDDDDDD);
        graphics.drawCenteredString(this.font, Component.translatable("safepaths.gui.conversions.title"), rightColCenter, 114, 0xDDDDDD);
    }
}
