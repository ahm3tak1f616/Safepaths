package com.ahmetakif.safepaths.client.gui;

import com.ahmetakif.safepaths.PathConversionRule;
import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class CustomConversionsScreen extends Screen {
    private enum View { LIST, EDIT, PALETTE }

    private final Screen parentScreen;
    private final Consumer<List<String>> apply;
    private final double globalSpeed;
    private int top;
    private int bottom;
    private int panelHeight;
    private final List<String> conversions;
    private final List<ResourceLocation> palette = new ArrayList<>();
    private final List<ResourceLocation> filteredBlocks = new ArrayList<>();
    private final List<PathWidgets.BlockButton> paletteButtons = new ArrayList<>();
    private View view = View.LIST;
    private int page;
    private int listPage;
    private int pageSize;
    private int editingIndex = -1;
    private boolean selectingSource;
    private String sourceText = "";
    private String targetText = "";
    private String speedText = "";
    private String searchText = "";
    private String problem;
    private EditBox sourceBox;
    private EditBox targetBox;
    private EditBox speedBox;
    private EditBox searchBox;
    private Button saveButton;
    private Button previousButton;
    private Button nextButton;
    private int left;
    private int contentWidth;

    public CustomConversionsScreen(Screen parentScreen, List<String> conversions, Consumer<List<String>> apply, double globalSpeed) {
        super(Component.translatable("safepaths.gui.conversions.title"));
        this.parentScreen = parentScreen;
        this.conversions = new ArrayList<>(conversions);
        this.apply = apply;
        this.globalSpeed = globalSpeed;
        for (ResourceLocation id : BuiltInRegistries.BLOCK.keySet()) {
            Block block = BuiltInRegistries.BLOCK.get(id);
            if (!block.asItem().getDefaultInstance().isEmpty()) palette.add(id);
        }
        palette.sort(Comparator.comparing(ResourceLocation::toString));
    }

    private Component text(String key, Object... args) {
        return Component.translatable("safepaths.gui." + key, args);
    }

    @Override
    protected void init() {
        captureInputs();
        left = Math.max(12, (width - 384) / 2);
        panelHeight = view == View.EDIT ? 200 : Math.min(height - 20, view == View.LIST ? 300 : 264);
        top = (height - panelHeight) / 2;
        bottom = top + panelHeight;
        contentWidth = width - left * 2;
        paletteButtons.clear();
        switch (view) {
            case LIST -> initList();
            case EDIT -> initEditor();
            case PALETTE -> initPalette();
        }
    }

    private void captureInputs() {
        if (view == View.EDIT && sourceBox != null) {
            sourceText = sourceBox.getValue();
            targetText = targetBox.getValue();
            speedText = speedBox.getValue();
        }
        if (view == View.PALETTE && searchBox != null) searchText = searchBox.getValue();
    }

    private void show(View nextView) {
        captureInputs();
        view = nextView;
        sourceBox = null;
        targetBox = null;
        speedBox = null;
        searchBox = null;
        init(minecraft, width, height);
    }

    private void initList() {
        pageSize = Math.max(1, (panelHeight - 108) / 34);
        page = Math.min(listPage, Math.max(0, (conversions.size() - 1) / pageSize));
        listPage = page;
        for (int row = 0; row < pageSize && page * pageSize + row < conversions.size(); row++) {
            int index = page * pageSize + row;
            String raw = conversions.get(index);
            PathConversionRule rule = PathConversionRule.parse(raw);
            Component tooltip = Component.literal(raw);
            if (rule != null) {
                String issue = rule.problem();
                if (issue != null) tooltip = tooltip.copy().append("\n").append(Component.translatable(issue));
            }
            String speed = rule == null ? raw : rule.speed() == null ? text("rule.global_short").getString() : text("rule.bonus", percent(rule.speed())).getString();
            boolean warning = rule == null || rule.problem() != null;
            PathWidgets.RuleButton rowButton = new PathWidgets.RuleButton(left, top + 32 + row * 34, contentWidth,
                    rule == null ? ItemStack.EMPTY : stack(rule.source()), rule == null ? ItemStack.EMPTY : stack(rule.target()),
                    rule == null ? raw : blockName(rule.source()), rule == null ? "?" : blockName(rule.target()),
                    speed, warning, button -> edit(index));
            rowButton.setTooltip(Tooltip.create(tooltip));
            addRenderableWidget(rowButton);
        }
        initPager(bottom - 76);
        int half = (contentWidth - 6) / 2;
        addRenderableWidget(Button.builder(text("rule.add"), button -> edit(-1))
                .bounds(left, bottom - 50, half, 20).build());
        addRenderableWidget(Button.builder(text("defaults"), button -> minecraft.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                conversions.clear();
                conversions.addAll(PathConfig.DEFAULT_CONVERSIONS);
                listPage = 0;
            }
            minecraft.setScreen(this);
        }, text("restore_defaults.title"), text("restore_defaults.message"), text("restore_defaults.confirm"), CommonComponents.GUI_CANCEL)))
                .bounds(left + half + 6, bottom - 50, half, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            apply.accept(List.copyOf(conversions));
            minecraft.setScreen(parentScreen);
        }).bounds(left, bottom - 26, half, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> minecraft.setScreen(parentScreen))
                .bounds(left + half + 6, bottom - 26, half, 20).build());
    }

    private void edit(int index) {
        editingIndex = index;
        sourceText = "";
        targetText = "";
        speedText = "";
        if (index >= 0) {
            String raw = conversions.get(index);
            String[] speedParts = raw.split("@", -1);
            String[] blocks = speedParts[0].split("->", -1);
            sourceText = blocks[0].trim();
            targetText = blocks.length > 1 ? blocks[1].trim() : "";
            if (speedParts.length > 1) {
                try {
                    speedText = percent(Double.parseDouble(speedParts[1].trim().replace(',', '.')));
                } catch (NumberFormatException ignored) {
                    speedText = speedParts[1].trim();
                }
            }
        }
        show(View.EDIT);
    }

    private void initEditor() {
        int browseWidth = 66;
        sourceBox = editBox(top + 42, contentWidth - browseWidth - 34, text("rule.source"), sourceText);
        targetBox = editBox(top + 78, contentWidth - browseWidth - 34, text("rule.target"), targetText);
        sourceBox.setX(left + 28);
        targetBox.setX(left + 28);
        speedBox = editBox(top + 114, 64, Component.translatable("config.safepaths.speedMultiplier"), speedText);
        speedBox.setMaxLength(32);
        speedBox.setTooltip(Tooltip.create(text("rule.speed_help")));
        addRenderableWidget(Button.builder(text("rule.browse"), button -> openPalette(true))
                .bounds(left + contentWidth - browseWidth, top + 42, browseWidth, 20).build());
        addRenderableWidget(Button.builder(text("rule.browse"), button -> openPalette(false))
                .bounds(left + contentWidth - browseWidth, top + 78, browseWidth, 20).build());
        int third = (contentWidth - 12) / 3;
        Button delete = addRenderableWidget(Button.builder(text("rule.delete"), button -> {
            conversions.remove(editingIndex);
            show(View.LIST);
        }).bounds(left, bottom - 26, third, 20).build());
        delete.active = editingIndex >= 0;
        saveButton = addRenderableWidget(Button.builder(text("rule.save"), button -> {
            PathConversionRule rule = editorRule();
            if (rule == null) return;
            if (editingIndex >= 0) conversions.set(editingIndex, rule.serialize());
            else {
                conversions.add(rule.serialize());
                listPage = (conversions.size() - 1) / Math.max(1, (panelHeight - 108) / 34);
            }
            show(View.LIST);
        }).bounds(left + third + 6, bottom - 26, third, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> show(View.LIST))
                .bounds(left + (third + 6) * 2, bottom - 26, third, 20).build());
        sourceBox.setResponder(value -> validateEditor());
        targetBox.setResponder(value -> validateEditor());
        speedBox.setResponder(value -> validateEditor());
        validateEditor();
        setInitialFocus(sourceBox);
    }

    private EditBox editBox(int y, int boxWidth, Component label, String value) {
        EditBox box = new EditBox(font, left, y, boxWidth, 20, label);
        box.setMaxLength(128);
        box.setValue(value);
        return addRenderableWidget(box);
    }

    private PathConversionRule editorRule() {
        String suffix = "";
        if (!speedBox.getValue().isBlank()) {
            try {
                double value = new BigDecimal(speedBox.getValue().trim().replace(',', '.')).movePointLeft(2).doubleValue();
                if (!Double.isFinite(value) || value < 0 || value > 5) return null;
                suffix = " @ " + value;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return PathConversionRule.parse(sourceBox.getValue().trim() + " -> " + targetBox.getValue().trim() + suffix);
    }

    private void validateEditor() {
        PathConversionRule rule = editorRule();
        problem = rule == null ? "safepaths.gui.rule.invalid" : rule.problem();
        if (rule != null) {
            for (int index = 0; index < conversions.size(); index++) {
                PathConversionRule other = PathConversionRule.parse(conversions.get(index));
                if (index != editingIndex && other != null && other.source().equals(rule.source())) {
                    problem = "safepaths.gui.rule.duplicate";
                    break;
                }
            }
        }
        saveButton.active = problem == null || "safepaths.gui.rule.missing".equals(problem);
        saveButton.setTooltip(problem == null ? null : Tooltip.create(Component.translatable(problem)));
    }

    private void openPalette(boolean source) {
        selectingSource = source;
        searchText = "";
        page = 0;
        show(View.PALETTE);
    }

    private void initPalette() {
        searchBox = editBox(top + 30, contentWidth, text("palette.search"), searchText);
        searchBox.setHint(text("palette.search_hint"));
        int columns = Math.max(1, contentWidth / 20);
        int rows = Math.max(1, (panelHeight - 106) / 20);
        pageSize = rows * columns;
        int gridLeft = left + (contentWidth - columns * 20) / 2;
        for (int index = 0; index < pageSize; index++) {
            final int slot = index;
            PathWidgets.BlockButton button = addRenderableWidget(new PathWidgets.BlockButton(
                    gridLeft + (index % columns) * 20, top + 54 + (index / columns) * 20, clicked -> {
                ResourceLocation chosen = filteredBlocks.get(page * pageSize + slot);
                if (selectingSource) sourceText = chosen.toString();
                else targetText = chosen.toString();
                show(View.EDIT);
            }));
            paletteButtons.add(button);
        }
        initPager(bottom - 52);
        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> show(View.EDIT))
                .bounds(left, bottom - 26, contentWidth, 20).build());
        searchBox.setResponder(query -> {
            searchText = query;
            page = 0;
            filterPalette();
        });
        filterPalette();
        setInitialFocus(searchBox);
    }

    private void filterPalette() {
        filteredBlocks.clear();
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        for (ResourceLocation id : palette) {
            if (id.toString().toLowerCase(Locale.ROOT).contains(query) || blockName(id).toLowerCase(Locale.ROOT).contains(query)) {
                filteredBlocks.add(id);
            }
        }
        page = Math.min(page, Math.max(0, (filteredBlocks.size() - 1) / pageSize));
        updatePaletteButtons();
    }

    private void updatePaletteButtons() {
        for (int slot = 0; slot < paletteButtons.size(); slot++) {
            PathWidgets.BlockButton button = paletteButtons.get(slot);
            int index = page * pageSize + slot;
            button.visible = index < filteredBlocks.size();
            if (!button.visible) continue;
            ResourceLocation id = filteredBlocks.get(index);
            Block block = BuiltInRegistries.BLOCK.get(id);
            boolean safe = selectingSource ? !PathConversionRule.isProtectedSource(block.defaultBlockState())
                    : PathConversionRule.isSafeRoad(block.defaultBlockState());
            button.active = safe;
            button.block(stack(id), Component.literal(blockName(id)), id.toString().equals(selectingSource ? sourceText : targetText));
            Component tooltip = Component.literal(blockName(id)).append("\n").append(Component.literal(id.toString()).withStyle(ChatFormatting.GRAY));
            if (!safe) tooltip = tooltip.copy().append("\n").append(text(selectingSource ? "rule.protected_source" : "rule.unsafe_target"));
            button.setTooltip(Tooltip.create(tooltip));
        }
        updatePager();
    }

    private void initPager(int y) {
        previousButton = addRenderableWidget(Button.builder(Component.literal("←"), button -> changePage(-1))
                .bounds(left, y, 36, 20).build());
        nextButton = addRenderableWidget(Button.builder(Component.literal("→"), button -> changePage(1))
                .bounds(left + contentWidth - 36, y, 36, 20).build());
        updatePager();
    }

    private void updatePager() {
        int count = view == View.PALETTE ? filteredBlocks.size() : conversions.size();
        previousButton.active = page > 0;
        nextButton.active = (page + 1) * pageSize < count;
    }

    private void changePage(int direction) {
        page += direction;
        if (view == View.LIST) {
            listPage = page;
            show(View.LIST);
        } else updatePaletteButtons();
    }

    private ItemStack stack(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.containsKey(id) ? BuiltInRegistries.BLOCK.get(id).asItem().getDefaultInstance() : ItemStack.EMPTY;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (view == View.EDIT) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (scrollY < 0 && nextButton.active) changePage(1);
        else if (scrollY > 0 && previousButton.active) changePage(-1);
        return true;
    }

    private String blockName(ResourceLocation id) {
        if (!BuiltInRegistries.BLOCK.containsKey(id)) return id.toString();
        Block block = BuiltInRegistries.BLOCK.get(id);
        String name = block.getName().getString();
        if (!name.equals(block.getDescriptionId())) return name;
        StringBuilder readable = new StringBuilder();
        for (String word : id.getPath().replace('/', '_').split("_")) {
            if (!word.isEmpty()) readable.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
        }
        return readable.toString().trim();
    }

    private static String percent(double multiplier) {
        return BigDecimal.valueOf(multiplier).multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString();
    }

    @Override
    public void onClose() {
        switch (view) {
            case LIST -> minecraft.setScreen(parentScreen);
            case EDIT -> show(View.LIST);
            case PALETTE -> show(View.EDIT);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        renderPanel(graphics);
    }

    private void renderPanel(GuiGraphics graphics) {
        PathWidgets.panel(graphics, left - 10, top, contentWidth + 20, panelHeight);
        Component heading = view == View.PALETTE ? text(selectingSource ? "rule.source" : "rule.target") : view == View.EDIT ? text("rule.edit") : title;
        graphics.drawString(font, heading, width / 2 - font.width(heading) / 2, top + 10, 0x303030, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (view == View.EDIT) {
            graphics.drawString(font, text("rule.source"), left, top + 30, 0x404040, false);
            graphics.drawString(font, text("rule.target"), left, top + 66, 0x404040, false);
            graphics.drawString(font, Component.translatable("config.safepaths.speedMultiplier"), left, top + 102, 0x404040, false);
            graphics.drawString(font, "%", left + 70, top + 120, 0x555555, false);
            if (speedBox.getValue().isBlank()) {
                String inherited = text("rule.inherit", percent(globalSpeed)).getString();
                graphics.drawString(font, font.plainSubstrByWidth(inherited, contentWidth - 100), left + 100, top + 120, 0x555555, false);
            }
            Component detail = problem == null ? text("rule.speed_help") : Component.translatable(problem);
            int availableLines = Math.max(0, (panelHeight - 178) / font.lineHeight);
            var lines = font.split(detail, contentWidth);
            for (int index = 0; index < Math.min(lines.size(), availableLines); index++) {
                graphics.drawString(font, lines.get(index), left, top + 146 + index * font.lineHeight, problem == null ? 0x555555 : 0x884400, false);
            }
        } else {
            int count = view == View.PALETTE ? filteredBlocks.size() : conversions.size();
            int pages = Math.max(1, (count + pageSize - 1) / pageSize);
            int y = view == View.PALETTE ? bottom - 46 : bottom - 70;
            Component pageLabel = text("rule.page", page + 1, pages);
            graphics.drawString(font, pageLabel, width / 2 - font.width(pageLabel) / 2, y, 0x404040, false);
        }
        if (view == View.EDIT) {
            PathWidgets.slot(graphics, left, top + 41, 22);
            PathWidgets.slot(graphics, left, top + 77, 22);
            ResourceLocation source = ResourceLocation.tryParse(sourceBox.getValue());
            ResourceLocation target = ResourceLocation.tryParse(targetBox.getValue());
            if (source != null) graphics.renderFakeItem(stack(source), left + 3, top + 44);
            if (target != null) graphics.renderFakeItem(stack(target), left + 3, top + 80);
        }
        if (view == View.LIST && conversions.isEmpty()) {
            graphics.drawString(font, text("rule.empty"), left, top + 36, 0x555555, false);
        }
        if (view == View.PALETTE && filteredBlocks.isEmpty()) {
            graphics.drawString(font, text("palette.empty"), left, top + 66, 0x555555, false);
        }
    }
}
