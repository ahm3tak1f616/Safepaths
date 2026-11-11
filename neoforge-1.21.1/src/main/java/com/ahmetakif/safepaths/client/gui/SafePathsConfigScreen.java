package com.ahmetakif.safepaths.client.gui;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class SafePathsConfigScreen extends Screen {
    private final Screen parentScreen;
    private final Map<String, String> numbers = new LinkedHashMap<>();
    private final Map<String, Boolean> toggles = new LinkedHashMap<>();
    private final Map<String, EditBox> boxes = new LinkedHashMap<>();
    private final String[] categories = {"creation", "decay", "speed"};
    private final List<String> conversions = new ArrayList<>(PathConfig.CUSTOM_CONVERSIONS.get());
    private int top;
    private int bottom;
    private int category;
    private int left;
    private int contentWidth;
    private Button done;
    private String invalidKey;

    public SafePathsConfigScreen(Screen parentScreen) {
        super(Component.translatable("safepaths.configuration.title"));
        this.parentScreen = parentScreen;
        numbers.put("requiredPasses", String.valueOf(PathConfig.REQUIRED_PASSES.get()));
        numbers.put("constructionTime", String.valueOf(PathConfig.CONSTRUCTION_TIME.get()));
        numbers.put("decayTime", String.valueOf(PathConfig.DECAY_TIME.get()));
        numbers.put("speedMultiplier", BigDecimal.valueOf(PathConfig.SPEED_MULTIPLIER.get()).movePointRight(2).stripTrailingZeros().toPlainString());
        toggles.put("enablePathCreation", PathConfig.ENABLE_PATH_CREATION.get());
        toggles.put("enablePathDecay", PathConfig.ENABLE_PATH_DECAY.get());
        toggles.put("sneakPreventsPathCreation", PathConfig.SNEAK_PREVENTS_PATH_CREATION.get());
        toggles.put("enableSpeedBoost", PathConfig.ENABLE_SPEED_BOOST.get());
    }

    private Component label(String key) {
        return Component.translatable("config.safepaths." + key);
    }

    private void captureInputs() {
        boxes.forEach((key, box) -> numbers.put(key, box.getValue()));
    }

    private void rebuild() {
        captureInputs();
        boxes.clear();
        init(minecraft, width, height);
    }

    @Override
    protected void init() {
        captureInputs();
        boxes.clear();
        left = Math.max(12, (width - 384) / 2);
        int panelHeight = category == 0 ? 208 : 160;
        top = (height - 208) / 2;
        bottom = top + panelHeight;
        contentWidth = width - left * 2;
        int third = (contentWidth - 12) / 3;
        for (int index = 0; index < categories.length; index++) {
            int selected = index;
            Button tab = addRenderableWidget(Button.builder(Component.translatable("safepaths.gui.settings." + categories[index]), button -> {
                category = selected;
                rebuild();
            }).bounds(left + index * (third + 6), top + 28, third, 20).build());
            tab.active = index != category;
        }
        switch (category) {
            case 0 -> {
                toggle("enablePathCreation", 0);
                number("requiredPasses", 1);
                number("constructionTime", 2);
                toggle("sneakPreventsPathCreation", 3);
            }
            case 1 -> {
                toggle("enablePathDecay", 0);
                number("decayTime", 1);
            }
            case 2 -> {
                toggle("enableSpeedBoost", 0);
                number("speedMultiplier", 1);
            }
        }
        addRenderableWidget(Button.builder(Component.translatable("safepaths.gui.conversions.configure"), button -> {
            captureInputs();
            minecraft.setScreen(new CustomConversionsScreen(this, conversions, updated -> {
                conversions.clear();
                conversions.addAll(updated);
            }, valid("speedMultiplier") ? speedValue() : PathConfig.SPEED_MULTIPLIER.get()));
        }).bounds(left, bottom - 52, contentWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("config.safepaths.customConversions.tooltip"))).build());
        addRenderableWidget(Button.builder(Component.translatable("safepaths.gui.defaults"), button -> {
            captureInputs();
            minecraft.setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed) restoreDefaults();
                minecraft.setScreen(this);
            }, Component.translatable("safepaths.gui.settings.reset_title"),
                    Component.translatable("safepaths.gui.settings.reset_message"),
                    Component.translatable("safepaths.gui.restore_defaults.confirm"), CommonComponents.GUI_CANCEL));
        }).bounds(left, bottom - 26, third, 20).build());
        done = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> saveAndClose())
                .bounds(left + third + 6, bottom - 26, third, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(left + (third + 6) * 2, bottom - 26, third, 20).build());
        validate();
    }

    private void restoreDefaults() {
        numbers.put("requiredPasses", String.valueOf(PathConfig.REQUIRED_PASSES.getDefault()));
        numbers.put("constructionTime", String.valueOf(PathConfig.CONSTRUCTION_TIME.getDefault()));
        numbers.put("decayTime", String.valueOf(PathConfig.DECAY_TIME.getDefault()));
        numbers.put("speedMultiplier", BigDecimal.valueOf(PathConfig.SPEED_MULTIPLIER.getDefault()).movePointRight(2).stripTrailingZeros().toPlainString());
        toggles.put("enablePathCreation", PathConfig.ENABLE_PATH_CREATION.getDefault());
        toggles.put("enablePathDecay", PathConfig.ENABLE_PATH_DECAY.getDefault());
        toggles.put("sneakPreventsPathCreation", PathConfig.SNEAK_PREVENTS_PATH_CREATION.getDefault());
        toggles.put("enableSpeedBoost", PathConfig.ENABLE_SPEED_BOOST.getDefault());
        conversions.clear();
        conversions.addAll(PathConfig.DEFAULT_CONVERSIONS);
        boxes.clear();
    }

    private void number(String key, int row) {
        int boxWidth = 64;
        EditBox box = new EditBox(font, left + contentWidth - 92, top + 58 + row * 24, boxWidth, 20, label(key));
        box.setMaxLength(32);
        box.setValue(numbers.get(key));
        box.setTooltip(Tooltip.create(Component.translatable("config.safepaths." + key + ".tooltip")));
        boxes.put(key, box);
        addRenderableWidget(box);
        box.setResponder(value -> {
            numbers.put(key, value);
            validate();
        });
    }

    private Component toggleLabel(String key) {
        return Component.translatable(toggles.get(key) ? "options.on" : "options.off");
    }

    private void toggle(String key, int row) {
        addRenderableWidget(Button.builder(toggleLabel(key), button -> {
            toggles.put(key, !toggles.get(key));
            button.setMessage(toggleLabel(key));
        }).bounds(left + contentWidth - 92, top + 58 + row * 24, 64, 20)
                .tooltip(Tooltip.create(Component.translatable("config.safepaths." + key + ".tooltip"))).build());
    }

    private double value(String key) {
        return new BigDecimal(numbers.get(key).trim().replace(',', '.')).doubleValue();
    }

    private double speedValue() {
        return new BigDecimal(numbers.get("speedMultiplier").trim().replace(',', '.')).movePointLeft(2).doubleValue();
    }

    private boolean valid(String key) {
        try {
            double value = value(key);
            if (!Double.isFinite(value)) return false;
            return switch (key) {
                case "requiredPasses" -> value == Math.rint(value) && value >= 1 && value <= 1000;
                case "constructionTime" -> value == Math.rint(value) && value >= 1000 && value <= 72000;
                case "decayTime" -> value == Math.rint(value) && value >= 1000 && value <= 240000;
                case "speedMultiplier" -> value >= 0 && value <= 500;
                default -> false;
            };
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private void validate() {
        invalidKey = null;
        for (String key : numbers.keySet()) {
            if (!valid(key)) {
                invalidKey = key;
                break;
            }
        }
        boxes.forEach((key, box) -> box.setTextColor(valid(key) ? 0xE0E0E0 : 0xFF7777));
        if (done != null) {
            done.active = invalidKey == null;
            done.setTooltip(invalidKey == null ? null : Tooltip.create(Component.translatable("safepaths.gui.settings.invalid", label(invalidKey))));
        }
    }

    private void saveAndClose() {
        captureInputs();
        validate();
        if (invalidKey != null) return;
        PathConfig.REQUIRED_PASSES.set((int) value("requiredPasses"));
        PathConfig.CONSTRUCTION_TIME.set((int) value("constructionTime"));
        PathConfig.DECAY_TIME.set((int) value("decayTime"));
        PathConfig.SPEED_MULTIPLIER.set(speedValue());
        PathConfig.ENABLE_PATH_CREATION.set(toggles.get("enablePathCreation"));
        PathConfig.ENABLE_PATH_DECAY.set(toggles.get("enablePathDecay"));
        PathConfig.SNEAK_PREVENTS_PATH_CREATION.set(toggles.get("sneakPreventsPathCreation"));
        PathConfig.ENABLE_SPEED_BOOST.set(toggles.get("enableSpeedBoost"));
        PathConfig.CUSTOM_CONVERSIONS.set(List.copyOf(conversions));
        PathConfig.SPEC.save();
        onClose();
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        renderPanel(graphics);
    }

    private void renderPanel(GuiGraphics graphics) {
        PathWidgets.panel(graphics, left - 10, top, contentWidth + 20, bottom - top);
        graphics.drawString(font, title, width / 2 - font.width(title) / 2, top + 10, 0x303030, false);
        String[] keys = switch (category) {
            case 0 -> new String[]{"enablePathCreation", "requiredPasses", "constructionTime", "sneakPreventsPathCreation"};
            case 1 -> new String[]{"enablePathDecay", "decayTime"};
            default -> new String[]{"enableSpeedBoost", "speedMultiplier"};
        };
        for (int row = 0; row < keys.length; row++) {
            int y = top + 58 + row * 24;
            graphics.fill(left - 3, y - 2, left + contentWidth + 3, y + 22, row % 2 == 0 ? 0xFFB5B5B5 : 0xFFBCBCBC);
            if (toggles.containsKey(keys[row])) {
                String name = font.plainSubstrByWidth(label(keys[row]).getString(), contentWidth - 104);
                graphics.drawString(font, name, left + 3, y + 6, 0x303030, false);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        boxes.forEach((key, box) -> {
            String name = label(key).getString();
            graphics.drawString(font, font.plainSubstrByWidth(name, box.getX() - left - 10), left + 3, box.getY() + 6, 0x303030, false);
        });
        boxes.forEach((key, box) -> {
            Component unit = key.equals("speedMultiplier") ? Component.literal("%")
                    : key.equals("requiredPasses") ? Component.empty() : Component.translatable("safepaths.gui.unit.ticks");
            graphics.drawString(font, unit, box.getX() + box.getWidth() + 5, box.getY() + 6, 0x555555, false);
        });
    }
}
