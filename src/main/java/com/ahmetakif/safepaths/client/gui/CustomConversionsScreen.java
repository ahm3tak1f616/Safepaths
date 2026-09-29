package com.ahmetakif.safepaths.client.gui;

import com.ahmetakif.safepaths.config.PathConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CustomConversionsScreen extends Screen {
    private final Screen parentScreen;
    private ConversionTableWidget conversionTable;
    private BlockPaletteWidget blockPalette;
    private EditBox searchBox;
    private Button speedBoostBtn;
    private EditBox masterSpeedBox;
    private boolean speedBoostEnabled;
    private int selectedEntryIndex = -1;
    private boolean selectingSource = true;
    private ResourceLocation lastSelectedBlock = null;
    private long lastSelectedTime = 0;

    private static final Set<Block> OPERATOR_AND_TECHNICAL_BLOCKS = Set.of(
            Blocks.COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK,
            Blocks.REPEATING_COMMAND_BLOCK,
            Blocks.STRUCTURE_BLOCK,
            Blocks.STRUCTURE_VOID,
            Blocks.JIGSAW,
            Blocks.BARRIER,
            Blocks.LIGHT,
            Blocks.MOVING_PISTON
    );

    public CustomConversionsScreen(Screen parentScreen) {
        super(Component.translatable("safepaths.gui.conversions.title"));
        this.parentScreen = parentScreen;
        this.speedBoostEnabled = PathConfig.ENABLE_SPEED_BOOST.get();
    }

    private Component getSpeedBoostButtonText() {
        return this.speedBoostEnabled
                ? Component.translatable("safepaths.gui.speed_boost.on")
                : Component.translatable("safepaths.gui.speed_boost.off");
    }

    @Override
    protected void init() {
        int contentTop = 38;
        int footerHeight = 36;
        int availableHeight = this.height - contentTop - footerHeight;

        int tableWidth = Math.max(180, (int) (this.width * 0.54));
        int paletteLeft = tableWidth + 8;
        int paletteWidth = Math.max(120, this.width - paletteLeft - 8);

        List<String> currentConversions;
        if (this.conversionTable != null) {
            currentConversions = this.conversionTable.gatherValues();
        } else {
            currentConversions = new ArrayList<>(PathConfig.CUSTOM_CONVERSIONS.get());
        }

        this.conversionTable = new ConversionTableWidget(this.minecraft, tableWidth, availableHeight, contentTop, 28);
        this.conversionTable.populate(currentConversions);
        this.addRenderableWidget(this.conversionTable);

        this.searchBox = new EditBox(this.font, paletteLeft, contentTop, paletteWidth, 18, Component.translatable("safepaths.gui.palette.search"));
        this.searchBox.setHint(Component.translatable("safepaths.gui.palette.search_hint"));
        this.searchBox.setResponder(text -> {
            if (this.blockPalette != null) {
                this.blockPalette.filter(text);
            }
        });
        this.addRenderableWidget(this.searchBox);

        int paletteTop = contentTop + 22;
        int paletteHeight = availableHeight - 22;
        this.blockPalette = new BlockPaletteWidget(this.minecraft, paletteLeft, paletteTop, paletteWidth, paletteHeight);
        this.addRenderableWidget(this.blockPalette);

        int buttonY = this.height - 28;
        int speedBoxWidth = 36;
        int speedIconWidth = 10;
        int totalActionWidth = tableWidth - 4;
        int leftControlsWidth = totalActionWidth - (speedBoxWidth + speedIconWidth + 8);
        int leftBtnWidth = Math.max(50, (leftControlsWidth - 8) / 3);

        int currentBtnX = 4;
        this.addRenderableWidget(Button.builder(Component.translatable("safepaths.gui.add_conversion"), b -> {
            this.conversionTable.addNewEntry("minecraft:grass_block", "minecraft:dirt_path");
            this.selectedEntryIndex = this.conversionTable.getEntryCount() - 1;
            this.selectingSource = false;
        }).bounds(currentBtnX, buttonY, leftBtnWidth, 20).build());

        currentBtnX += leftBtnWidth + 4;
        this.addRenderableWidget(Button.builder(Component.translatable("safepaths.gui.restore_defaults"), b -> {
            this.minecraft.setScreen(new ConfirmScreen(
                    confirmed -> {
                        if (confirmed) {
                            this.conversionTable.populate(PathConfig.DEFAULT_CONVERSIONS);
                            this.selectedEntryIndex = -1;
                        }
                        this.minecraft.setScreen(this);
                    },
                    Component.translatable("safepaths.gui.restore_defaults.title"),
                    Component.translatable("safepaths.gui.restore_defaults.message"),
                    Component.translatable("safepaths.gui.restore_defaults.confirm"),
                    CommonComponents.GUI_CANCEL
            ));
        }).bounds(currentBtnX, buttonY, leftBtnWidth, 20).build());

        currentBtnX += leftBtnWidth + 4;
        this.speedBoostBtn = Button.builder(getSpeedBoostButtonText(), b -> {
            this.speedBoostEnabled = !this.speedBoostEnabled;
            this.speedBoostBtn.setMessage(getSpeedBoostButtonText());
        }).bounds(currentBtnX, buttonY, leftBtnWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("config.safepaths.enableSpeedBoost.tooltip")))
                .build();
        this.addRenderableWidget(this.speedBoostBtn);

        currentBtnX += leftBtnWidth + 4;
        int speedBoxX = currentBtnX + speedIconWidth + 4;
        this.masterSpeedBox = new EditBox(this.font, speedBoxX, buttonY + 1, speedBoxWidth, 18, Component.translatable("config.safepaths.speedMultiplier"));
        this.masterSpeedBox.setMaxLength(5);
        this.masterSpeedBox.setValue(String.format(Locale.ROOT, "%.2f", PathConfig.SPEED_MULTIPLIER.get()));
        this.masterSpeedBox.setTooltip(Tooltip.create(Component.translatable("config.safepaths.speedMultiplier.tooltip")));
        this.addRenderableWidget(this.masterSpeedBox);

        int rightBtnWidth = Math.min(80, (this.width - paletteLeft - 10) / 2);
        int doneX = this.width - (rightBtnWidth * 2 + 8);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
            saveAndClose();
        }).bounds(doneX, buttonY, rightBtnWidth, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> {
            this.minecraft.setScreen(this.parentScreen);
        }).bounds(doneX + rightBtnWidth + 4, buttonY, rightBtnWidth, 20).build());
    }

    private void saveAndClose() {
        List<String> values = this.conversionTable.gatherValues();
        PathConfig.ENABLE_SPEED_BOOST.set(this.speedBoostEnabled);

        try {
            double multiplier = Double.parseDouble(this.masterSpeedBox.getValue().trim());
            multiplier = Mth.clamp(multiplier, 0.0, 5.0);
            PathConfig.SPEED_MULTIPLIER.set(multiplier);
        } catch (NumberFormatException ignored) {
        }

        PathConfig.CUSTOM_CONVERSIONS.set(values);
        PathConfig.SPEC.save();
        this.minecraft.setScreen(this.parentScreen);
    }

    public void selectSlot(int entryIndex, boolean isSource) {
        this.selectedEntryIndex = entryIndex;
        this.selectingSource = isSource;
    }

    public void onBlockChosenFromPalette(ResourceLocation blockId) {
        this.lastSelectedBlock = blockId;
        this.lastSelectedTime = System.currentTimeMillis();

        if (this.selectedEntryIndex >= 0 && this.selectedEntryIndex < this.conversionTable.getEntryCount()) {
            ConversionEntry entry = this.conversionTable.getEntry(this.selectedEntryIndex);
            if (this.selectingSource) {
                entry.sourceBox.setValue(blockId.toString());
                this.selectingSource = false;
            } else {
                entry.targetBox.setValue(blockId.toString());
            }
        } else {
            this.conversionTable.addNewEntry(blockId.toString(), "minecraft:dirt_path");
            this.selectedEntryIndex = this.conversionTable.getEntryCount() - 1;
            this.selectingSource = false;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderMenuBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 4, 10, 0xFFFFFF);
        graphics.drawString(this.font, Component.translatable("safepaths.gui.cheat_warning"), 8, 24, 0xFFFF55);

        int paletteTitleX = (int) (this.width * 0.54) + 8;
        graphics.drawString(this.font, Component.translatable("safepaths.gui.palette.title"), paletteTitleX, 10, 0xAAAAAA);

        if (this.masterSpeedBox != null) {
            int iconX = this.masterSpeedBox.getX() - 12;
            int iconY = this.masterSpeedBox.getY() + 5;
            graphics.drawString(this.font, "⚡", iconX, iconY, 0xFFFF55);
        }

        if (this.blockPalette != null) {
            this.blockPalette.renderHoverTooltip(graphics, mouseX, mouseY);
        }
        if (this.conversionTable != null) {
            this.conversionTable.renderSlotHoverTooltip(graphics, mouseX, mouseY);
        }
    }

    public class ConversionTableWidget extends ContainerObjectSelectionList<ConversionEntry> {
        public ConversionTableWidget(Minecraft mc, int width, int height, int y, int itemHeight) {
            super(mc, width, height, y, itemHeight);
        }

        public void populate(List<? extends String> entries) {
            this.clearEntries();
            for (String raw : entries) {
                if (raw == null || !raw.contains("->")) {
                    continue;
                }
                String entry = raw;
                if (raw.contains("@")) {
                    String[] speedParts = raw.split("@");
                    entry = speedParts[0].trim();
                }
                String[] parts = entry.split("->");
                if (parts.length >= 2) {
                    this.addEntry(new ConversionEntry(this, parts[0].trim(), parts[1].trim()));
                }
            }
        }

        public void addNewEntry(String source, String target) {
            this.addEntry(new ConversionEntry(this, source, target));
        }

        public int getEntryCount() {
            return this.getItemCount();
        }

        public void remove(ConversionEntry entry) {
            int idx = this.children().indexOf(entry);
            this.removeEntry(entry);
            if (CustomConversionsScreen.this.selectedEntryIndex == idx) {
                CustomConversionsScreen.this.selectedEntryIndex = -1;
            } else if (CustomConversionsScreen.this.selectedEntryIndex > idx) {
                CustomConversionsScreen.this.selectedEntryIndex--;
            }
        }

        public List<String> gatherValues() {
            List<String> list = new ArrayList<>();
            for (ConversionEntry entry : this.children()) {
                String mapping = entry.getMappingString();
                if (mapping != null && !mapping.isBlank()) {
                    list.add(mapping);
                }
            }
            return list;
        }

        public ConversionEntry getEntry(int index) {
            return this.children().get(index);
        }

        public void renderSlotHoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
            for (ConversionEntry entry : this.children()) {
                if (entry.renderHoverTooltip(graphics, mouseX, mouseY)) {
                    break;
                }
            }
        }

        @Override
        public int getRowWidth() {
            return this.width - 12;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width - 4;
        }

        @Override
        protected void renderListBackground(GuiGraphics graphics) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x44000000);
        }

        @Override
        protected void renderListSeparators(GuiGraphics graphics) {
        }
    }

    public class ConversionEntry extends ContainerObjectSelectionList.Entry<ConversionEntry> {
        private final ConversionTableWidget parentList;
        public final EditBox sourceBox;
        public final EditBox targetBox;
        public final Button deleteBtn;
        private int lastX;
        private int lastY;
        private int lastWidth;

        public ConversionEntry(ConversionTableWidget parentList, String source, String target) {
            this.parentList = parentList;

            this.sourceBox = new EditBox(CustomConversionsScreen.this.font, 0, 0, 70, 18, Component.literal("Source"));
            this.sourceBox.setMaxLength(128);
            this.sourceBox.setValue(source);

            this.targetBox = new EditBox(CustomConversionsScreen.this.font, 0, 0, 70, 18, Component.literal("Target"));
            this.targetBox.setMaxLength(128);
            this.targetBox.setValue(target);

            this.deleteBtn = Button.builder(Component.literal("✕"), b -> parentList.remove(this))
                    .bounds(0, 0, 18, 18)
                    .tooltip(Tooltip.create(Component.translatable("safepaths.gui.delete_conversion")))
                    .build();
        }

        public String getMappingString() {
            String src = this.sourceBox.getValue().trim();
            String tgt = this.targetBox.getValue().trim();
            if (src.isEmpty() || tgt.isEmpty()) {
                return null;
            }
            return src + " -> " + tgt;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.sourceBox, this.targetBox, this.deleteBtn);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.sourceBox, this.targetBox, this.deleteBtn);
        }

        private ItemStack getItemStackFor(String blockId) {
            ResourceLocation rl = ResourceLocation.tryParse(blockId);
            if (rl != null && BuiltInRegistries.BLOCK.containsKey(rl)) {
                Block b = BuiltInRegistries.BLOCK.get(rl);
                ItemStack stack = b.asItem().getDefaultInstance();
                if (!stack.isEmpty()) {
                    return stack;
                }
            }
            return Items.BARRIER.getDefaultInstance();
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            this.lastX = left;
            this.lastY = top;
            this.lastWidth = width;

            boolean isCurrentRowSelected = (CustomConversionsScreen.this.selectedEntryIndex == index);

            int slotSize = 18;
            int spacing = 4;
            int arrowWidth = 14;
            int deleteBtnWidth = 18;

            int fixedWidths = (slotSize * 2) + arrowWidth + deleteBtnWidth + (spacing * 5) + 4;
            int boxWidth = Math.max(30, (width - fixedWidths) / 2);

            int currentX = left + 2;
            int centerY = top + (height - slotSize) / 2;

            int sourceBorderColor = (isCurrentRowSelected && CustomConversionsScreen.this.selectingSource) ? 0xFF55FF55 : 0xFF888888;
            graphics.fill(currentX - 1, centerY - 1, currentX + slotSize + 1, centerY + slotSize + 1, sourceBorderColor);
            graphics.fill(currentX, centerY, currentX + slotSize, centerY + slotSize, 0xFF1E1E1E);
            ItemStack sourceStack = getItemStackFor(this.sourceBox.getValue().trim());
            graphics.renderFakeItem(sourceStack, currentX + 1, centerY + 1);

            currentX += slotSize + spacing;
            this.sourceBox.setX(currentX);
            this.sourceBox.setY(centerY);
            this.sourceBox.setWidth(boxWidth);
            this.sourceBox.render(graphics, mouseX, mouseY, partialTick);

            currentX += boxWidth + spacing;
            graphics.drawCenteredString(CustomConversionsScreen.this.font, "➔", currentX + 6, centerY + 5, 0xAAAAAA);

            currentX += arrowWidth + spacing;
            int targetBorderColor = (isCurrentRowSelected && !CustomConversionsScreen.this.selectingSource) ? 0xFF55FF55 : 0xFF888888;
            graphics.fill(currentX - 1, centerY - 1, currentX + slotSize + 1, centerY + slotSize + 1, targetBorderColor);
            graphics.fill(currentX, centerY, currentX + slotSize, centerY + slotSize, 0xFF1E1E1E);
            ItemStack targetStack = getItemStackFor(this.targetBox.getValue().trim());
            graphics.renderFakeItem(targetStack, currentX + 1, centerY + 1);

            currentX += slotSize + spacing;
            this.targetBox.setX(currentX);
            this.targetBox.setY(centerY);
            this.targetBox.setWidth(boxWidth);
            this.targetBox.render(graphics, mouseX, mouseY, partialTick);

            currentX += boxWidth + spacing;
            this.deleteBtn.setX(currentX);
            this.deleteBtn.setY(centerY);
            this.deleteBtn.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int slotSize = 18;
            int spacing = 4;
            int arrowWidth = 14;
            int deleteBtnWidth = 18;

            int fixedWidths = (slotSize * 2) + arrowWidth + deleteBtnWidth + (spacing * 5) + 4;
            int boxWidth = Math.max(30, (this.lastWidth - fixedWidths) / 2);

            int currentX = this.lastX + 2;
            int centerY = this.lastY + (28 - slotSize) / 2;

            int idx = this.parentList.children().indexOf(this);

            if (mouseX >= currentX && mouseX <= currentX + slotSize && mouseY >= centerY && mouseY <= centerY + slotSize) {
                CustomConversionsScreen.this.selectSlot(idx, true);
                return true;
            }

            int targetSlotX = currentX + slotSize + spacing + boxWidth + spacing + arrowWidth + spacing;
            if (mouseX >= targetSlotX && mouseX <= targetSlotX + slotSize && mouseY >= centerY && mouseY <= centerY + slotSize) {
                CustomConversionsScreen.this.selectSlot(idx, false);
                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        public boolean renderHoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
            int slotSize = 18;
            int spacing = 4;
            int arrowWidth = 14;
            int deleteBtnWidth = 18;

            int fixedWidths = (slotSize * 2) + arrowWidth + deleteBtnWidth + (spacing * 5) + 4;
            int boxWidth = Math.max(30, (this.lastWidth - fixedWidths) / 2);

            int currentX = this.lastX + 2;
            int centerY = this.lastY + (28 - slotSize) / 2;

            if (mouseX >= currentX && mouseX <= currentX + slotSize && mouseY >= centerY && mouseY <= centerY + slotSize) {
                ItemStack stack = getItemStackFor(this.sourceBox.getValue().trim());
                graphics.renderTooltip(CustomConversionsScreen.this.font, stack, mouseX, mouseY);
                return true;
            }

            int targetSlotX = currentX + slotSize + spacing + boxWidth + spacing + arrowWidth + spacing;
            if (mouseX >= targetSlotX && mouseX <= targetSlotX + slotSize && mouseY >= centerY && mouseY <= centerY + slotSize) {
                ItemStack stack = getItemStackFor(this.targetBox.getValue().trim());
                graphics.renderTooltip(CustomConversionsScreen.this.font, stack, mouseX, mouseY);
                return true;
            }

            return false;
        }
    }

    public class BlockPaletteWidget extends AbstractSelectionList<BlockPaletteWidget.BlockPaletteRow> {
        private final List<ResourceLocation> allBlocks = new ArrayList<>();
        private final List<ResourceLocation> filteredBlocks = new ArrayList<>();
        private final int columns;
        private final int slotSize = 18;
        private final int leftPos;

        public BlockPaletteWidget(Minecraft mc, int x, int y, int width, int height) {
            super(mc, width, height, y, 20);
            this.leftPos = x;
            this.setX(x);
            this.columns = Math.max(1, (width - 12) / slotSize);

            for (ResourceLocation key : BuiltInRegistries.BLOCK.keySet()) {
                Block b = BuiltInRegistries.BLOCK.get(key);
                if (b == Blocks.AIR || OPERATOR_AND_TECHNICAL_BLOCKS.contains(b)) {
                    continue;
                }

                BlockState state = b.defaultBlockState();
                if (!state.blocksMotion() || state.liquid()) {
                    continue;
                }

                ItemStack stack = b.asItem().getDefaultInstance();
                if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                    continue;
                }

                if (stack.getItem().getFoodProperties(stack, null) != null) {
                    continue;
                }

                allBlocks.add(key);
            }
            allBlocks.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));
            this.filteredBlocks.addAll(allBlocks);
            rebuildRows();
        }

        public void filter(String query) {
            this.filteredBlocks.clear();
            String q = query.trim().toLowerCase(Locale.ROOT);
            for (ResourceLocation rl : this.allBlocks) {
                if (q.isEmpty() || rl.toString().toLowerCase(Locale.ROOT).contains(q)) {
                    this.filteredBlocks.add(rl);
                } else {
                    Block b = BuiltInRegistries.BLOCK.get(rl);
                    String name = b.getName().getString().toLowerCase(Locale.ROOT);
                    if (name.contains(q)) {
                        this.filteredBlocks.add(rl);
                    }
                }
            }
            this.setScrollAmount(0);
            rebuildRows();
        }

        private void rebuildRows() {
            this.clearEntries();
            int total = this.filteredBlocks.size();
            for (int i = 0; i < total; i += columns) {
                int end = Math.min(i + columns, total);
                List<ResourceLocation> subList = this.filteredBlocks.subList(i, end);
                this.addEntry(new BlockPaletteRow(subList));
            }
        }

        @Override
        public int getRowWidth() {
            return this.width - 10;
        }

        @Override
        public int getRowLeft() {
            return this.leftPos + 4;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.leftPos + this.width - 6;
        }

        @Override
        protected void renderListBackground(GuiGraphics graphics) {
            graphics.fill(this.leftPos, this.getY(), this.leftPos + this.width, this.getY() + this.height, 0x44000000);
            graphics.renderOutline(this.leftPos, this.getY(), this.width, this.height, 0x66555555);
        }

        @Override
        protected void renderListSeparators(GuiGraphics graphics) {
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
        }

        public void renderHoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
            if (mouseX < this.leftPos || mouseX > this.leftPos + this.width || mouseY < this.getY() || mouseY > this.getY() + this.height) {
                return;
            }
            for (BlockPaletteRow row : this.children()) {
                if (row.renderTooltip(graphics, mouseX, mouseY)) {
                    break;
                }
            }
        }

        public class BlockPaletteRow extends AbstractSelectionList.Entry<BlockPaletteRow> {
            private final List<ResourceLocation> rowBlocks;
            private int lastTop;

            public BlockPaletteRow(List<ResourceLocation> rowBlocks) {
                this.rowBlocks = new ArrayList<>(rowBlocks);
            }

            @Override
            public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
                this.lastTop = top;
                long now = System.currentTimeMillis();

                for (int col = 0; col < rowBlocks.size(); col++) {
                    int slotX = left + col * slotSize;
                    int slotY = top + 1;

                    ResourceLocation rl = rowBlocks.get(col);
                    boolean hovered = mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize;
                    boolean isRecentlySelected = rl.equals(CustomConversionsScreen.this.lastSelectedBlock)
                            && (now - CustomConversionsScreen.this.lastSelectedTime < 1000);

                    int bg;
                    if (isRecentlySelected) {
                        float progress = (now - CustomConversionsScreen.this.lastSelectedTime) / 1000.0f;
                        int pulseAlpha = (int) (Mth.clamp(1.0f - progress, 0.0f, 1.0f) * 200);
                        bg = (pulseAlpha << 24) | 0x00FF55;
                    } else if (hovered) {
                        bg = 0xFF555555;
                    } else {
                        bg = 0xFF2A2A2A;
                    }

                    graphics.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, bg);
                    graphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0xFF141414);

                    if (hovered) {
                        graphics.renderOutline(slotX, slotY, slotSize, slotSize, 0xFFFFFFFF);
                    } else if (isRecentlySelected) {
                        graphics.renderOutline(slotX, slotY, slotSize, slotSize, 0xFF55FF55);
                    }

                    Block b = BuiltInRegistries.BLOCK.get(rl);
                    ItemStack stack = b.asItem().getDefaultInstance();
                    if (stack.isEmpty()) {
                        stack = Items.BARRIER.getDefaultInstance();
                    }
                    graphics.renderFakeItem(stack, slotX + 1, slotY + 1);
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                int left = BlockPaletteWidget.this.getRowLeft();
                if (mouseY >= this.lastTop + 1 && mouseY < this.lastTop + 1 + slotSize) {
                    int col = (int) ((mouseX - left) / slotSize);
                    if (col >= 0 && col < rowBlocks.size()) {
                        ResourceLocation chosen = rowBlocks.get(col);
                        CustomConversionsScreen.this.onBlockChosenFromPalette(chosen);
                        return true;
                    }
                }
                return false;
            }

            public boolean renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
                int left = BlockPaletteWidget.this.getRowLeft();
                if (mouseY >= this.lastTop + 1 && mouseY < this.lastTop + 1 + slotSize) {
                    int col = (int) ((mouseX - left) / slotSize);
                    if (col >= 0 && col < rowBlocks.size()) {
                        ResourceLocation rl = rowBlocks.get(col);
                        Block b = BuiltInRegistries.BLOCK.get(rl);
                        ItemStack stack = b.asItem().getDefaultInstance();
                        if (!stack.isEmpty()) {
                            graphics.renderTooltip(CustomConversionsScreen.this.font, stack, mouseX, mouseY);
                        } else {
                            graphics.renderTooltip(CustomConversionsScreen.this.font, Component.literal(rl.toString()), mouseX, mouseY);
                        }
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
