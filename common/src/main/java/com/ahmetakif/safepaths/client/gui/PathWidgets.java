package com.ahmetakif.safepaths.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

final class PathWidgets {
    private PathWidgets() {}

    static void panel(GuiGraphics g, int x, int y, int width, int height) {
        g.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF101010);
        g.fill(x, y, x + width, y + height, 0xFFC6C6C6);
        g.fill(x, y, x + width - 1, y + 2, 0xFFFFFFFF);
        g.fill(x, y, x + 2, y + height - 1, 0xFFFFFFFF);
        g.fill(x + 2, y + height - 2, x + width, y + height, 0xFF555555);
        g.fill(x + width - 2, y + 2, x + width, y + height, 0xFF555555);
    }

    static void slot(GuiGraphics g, int x, int y, int size) {
        g.fill(x, y, x + size, y + size, 0xFF373737);
        g.fill(x + 1, y + 1, x + size, y + size, 0xFFFFFFFF);
        g.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xFF8B8B8B);
    }

    static final class BlockButton extends Button {
        private ItemStack stack = ItemStack.EMPTY;
        private boolean selected;

        BlockButton(int x, int y, OnPress action) {
            super(x, y, 20, 20, Component.empty(), action, DEFAULT_NARRATION);
        }

        void block(ItemStack stack, Component name, boolean selected) {
            this.stack = stack;
            this.selected = selected;
            setMessage(name);
        }

        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            slot(g, getX(), getY(), 20);
            if (selected) g.fill(getX() + 1, getY() + 1, getX() + 19, getY() + 19, 0xFFADC59A);
            g.renderFakeItem(stack, getX() + 2, getY() + 2);
            if (!active) g.fill(getX() + 1, getY() + 1, getX() + 19, getY() + 19, 0x88777777);
            if (isHoveredOrFocused()) g.fill(getX() + 1, getY() + 1, getX() + 19, getY() + 19, 0x60FFFFFF);
        }
    }

    static final class RuleButton extends Button {
        private final ItemStack source;
        private final ItemStack target;
        private final String sourceName;
        private final String targetName;
        private final String speed;
        private final boolean warning;

        RuleButton(int x, int y, int width, ItemStack source, ItemStack target, String sourceName,
                   String targetName, String speed, boolean warning, OnPress action) {
            super(x, y, width, 32, Component.literal(sourceName + " → " + targetName + ", " + speed), action, DEFAULT_NARRATION);
            this.source = source;
            this.target = target;
            this.sourceName = sourceName;
            this.targetName = targetName;
            this.speed = speed;
            this.warning = warning;
        }

        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            var font = Minecraft.getInstance().font;
            int x = getX(), y = getY(), half = (getWidth() - 16) / 2;
            g.fill(x, y, x + getWidth(), y + 32, isHoveredOrFocused() ? 0xFFADBDCB : 0xFFB3B3B3);
            g.fill(x, y + 31, x + getWidth(), y + 32, 0xFF999999);
            slot(g, x + 4, y + 5, 22);
            slot(g, x + half + 20, y + 5, 22);
            g.renderFakeItem(source, x + 7, y + 8);
            g.renderFakeItem(target, x + half + 23, y + 8);
            g.drawString(font, font.plainSubstrByWidth(sourceName, half - 34), x + 31, y + 7, 0x303030, false);
            g.drawString(font, "→", x + half + 3, y + 11, 0x404040, false);
            g.drawString(font, font.plainSubstrByWidth(targetName, half - 38), x + half + 47, y + 5, 0x303030, false);
            g.drawString(font, font.plainSubstrByWidth(speed, half - 38), x + half + 47, y + 18, warning ? 0x884400 : 0x555555, false);
        }
    }
}
