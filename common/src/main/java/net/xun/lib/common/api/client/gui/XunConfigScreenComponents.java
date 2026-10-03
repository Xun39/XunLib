package net.xun.lib.common.api.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.ConfigDefinition;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigTheme;

import java.util.List;
import java.util.function.IntConsumer;

public final class XunConfigScreenComponents {
    // ---------------------------------------------------------------------
    // Category button
    // ---------------------------------------------------------------------

    static final class CategoryButton extends AbstractButton {
        private final XunConfigTheme theme;
        private final IntConsumer onPress;

        private boolean selected;

        CategoryButton(Component message, XunConfigTheme theme, boolean selected, IntConsumer onPress) {
            super(0, 0, 100, 22, message);

            this.theme = theme;
            this.selected = selected;
            this.onPress = onPress;
        }

        void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void onPress() {
            onPress.accept(0);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            boolean hovered = isHoveredOrFocused();

            if (selected) {
                graphics.fill(getX(), getY(), getRight(), getBottom(), theme.sidebarSelected());
                graphics.fill(getX(), getY() + 3, getX() + 2, getBottom() - 3, theme.accent());
            }
            else if (hovered) {
                graphics.fill(getX(), getY(), getRight(), getBottom(), theme.sidebarHover());
            }

            int color = selected ? theme.text() : enabledTextColor();

            AbstractWidget.renderScrollingString(graphics, Minecraft.getInstance().font, getMessage(), getX() + 10, getY() + 6, getRight() - 8, getBottom() - 5, color);
        }

        private int enabledTextColor() {
            return active ? theme.textMuted() : theme.textDisabled();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }

    // ---------------------------------------------------------------------
    // Save / action button
    // ---------------------------------------------------------------------

    static final class ActionButton extends AbstractButton {
        private final XunConfigTheme theme;
        private final Runnable onPress;

        ActionButton(Component message, XunConfigTheme theme, Runnable onPress) {
            super(0, 0, 76, 22, message);

            this.theme = theme;
            this.onPress = onPress;
        }

        @Override
        public void onPress() {
            onPress.run();
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            boolean hovered = isHoveredOrFocused();

            int background = hovered ? theme.accentSecondary() : theme.accent();

            graphics.fill(getX(), getY(), getRight(), getBottom(), background);
            graphics.fill(getX(), getY(), getRight(), getY() + 1, 0x45FFFFFF);

            AbstractWidget.renderScrollingString(graphics, Minecraft.getInstance().font, getMessage(), getX() + 8, getY() + 6, getRight() - 8, getBottom() - 5, theme.text());
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }

    // ---------------------------------------------------------------------
    // Option button
    // ---------------------------------------------------------------------

    static final class OptionButton extends AbstractButton {
        private final XunConfigScreen owner;
        private final ConfigOption option;
        private final XunConfigTheme theme;

        OptionButton(XunConfigScreen owner, ConfigOption option, XunConfigTheme theme) {
            super(0, 0, 100, 40, Component.translatableWithFallback(option.nameKey, option.nameFallback));

            this.owner = owner;
            this.option = option;
            this.theme = theme;

            this.active = option.isVisible() && isBoolean();
        }

        private boolean isBoolean() {
            return option.type == boolean.class || option.type == Boolean.class;
        }

        @Override
        public void onPress() {
            if (!option.isVisible() || !isBoolean()) {
                return;
            }

            option.setValue(!Boolean.TRUE.equals(option.getValue()));
        }

        public boolean isActiveForOption() {
            return this.active;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Keep the screen's information panel in sync.
            if (isHovered()) {
                owner.setHoveredOption(option);
            }

            boolean enabled = option.isVisible();
            boolean hovered = enabled && isHoveredOrFocused();

            int background = !enabled ? theme.cardDisabled() : hovered ? theme.cardHover() : theme.cardBackground();

            int border = !enabled ? 0x10FFFFFF : hovered ? theme.accent() : theme.cardBorder();

            // Small shadow
            graphics.fill(getX() + 1, getY() + 2, getRight() + 1, getBottom() + 1, theme.panelShadow());

            // Card
            graphics.fill(getX(), getY(), getRight(), getBottom(), background);

            // Border
            graphics.fill(getX(), getY(), getRight(), getY() + 1, border);
            graphics.fill(getX(), getBottom() - 1, getRight(), getBottom(), border);
            graphics.fill(getX(), getY(), getX() + 1, getBottom(), border);
            graphics.fill(getRight() - 1, getY(), getRight(), getBottom(), border);

            // Hover accent strip
            if (hovered) {
                graphics.fill(getX(), getY() + 4, getX() + 2, getBottom() - 4, theme.accent());
            }

            int textColor = enabled ? theme.text() : theme.textDisabled();
            int mutedColor = enabled ? theme.textMuted() : theme.textDisabled();

            Minecraft minecraft = Minecraft.getInstance();

            int textRight = getRight() - 58;

            // Title
            AbstractWidget.renderScrollingString(graphics, minecraft.font, getMessage(), getX() + 11, getY() + 6, textRight, getY() + 19, textColor);

            // Description.
            Component description = Component.translatableWithFallback(option.descriptionKey, option.descriptionFallback);

            String descriptionText = truncate(minecraft, description.getString(), Math.max(30, textRight - (getX() + 11)));

            graphics.drawString(minecraft.font, descriptionText, getX() + 11, getY() + 22, mutedColor, false);

            if (isBoolean()) {
                drawToggle(graphics, getRight() - 44, getY() + 11, 34, 16, Boolean.TRUE.equals(option.getValue()), enabled);
            }
            else {
                drawValue(graphics, minecraft, getX(), getY(), getRight(), getBottom(), option.getValue(), mutedColor);
            }
        }

        private static String truncate(Minecraft minecraft, String text, int maxWidth) {
            if (minecraft.font.width(text) <= maxWidth) {
                return text;
            }

            String shortened = minecraft.font.plainSubstrByWidth(text, Math.max(0, maxWidth - 7));
            return shortened + "...";
        }

        private static void drawValue(GuiGraphics graphics, Minecraft minecraft, int left, int top, int right, int bottom, Object value, int color) {
            if (value == null) {
                return;
            }

            String text = String.valueOf(value);

            int width = minecraft.font.width(text);

            graphics.drawString(minecraft.font, text, right - width - 12, top + 14, color, false);
        }

        private void drawToggle(GuiGraphics graphics, int x, int y, int width, int height, boolean on, boolean enabled) {
            int track = !enabled ? theme.cardDisabled() : on ? theme.toggleOn() : theme.toggleOff();

            graphics.fill(x, y, x + width, y + height, track);

            if (enabled && on) {
                graphics.fill(x, y, x + width, y + 1, 0x40FFFFFF);
            }

            int knobSize = height - 4;
            int knobX = on ? x + width - knobSize - 2 : x + 2;

            graphics.fill(knobX + 1, y + 3, knobX + knobSize - 1, y + height - 1, 0x50000000);
            graphics.fill(knobX, y + 2, knobX + knobSize, y + height - 2, enabled ? theme.toggleKnob() : theme.textDisabled());
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }

    // ---------------------------------------------------------------------
    // Category list
    // ---------------------------------------------------------------------

    static final class CategoryList extends ContainerObjectSelectionList<CategoryEntry> {
        private static final int ROW_HEIGHT = 28;

        private final XunConfigScreen owner;
        private final ConfigDefinition config;
        private final XunConfigTheme theme;

        CategoryList(XunConfigScreen owner, ConfigDefinition config, XunConfigTheme theme, Minecraft minecraft, int x, int y, int width, int height) {
            super(minecraft, width, height, y, ROW_HEIGHT);

            this.owner = owner;
            this.config = config;
            this.theme = theme;

            setX(x);
        }

        void rebuild(int selectedIndex) {
            clearEntries();

            List<String> categories = config.getCategoryKeys();

            for (int i = 0; i < categories.size(); i++) {
                String categoryKey = categories.get(i);

                addEntry(new CategoryEntry(owner, config, theme, categoryKey, i, i == selectedIndex));
            }

            setScrollAmount(0);
        }

        void updateSelection(int selectedIndex) {
            for (int i = 0; i < children().size(); i++) {
                children().get(i).setSelected(i == selectedIndex);
            }
        }

        @Override
        public int getRowLeft() {
            return getX() + 4;
        }

        @Override
        public int getRowRight() {
            return getX() + getWidth() - 10;
        }

        @Override
        public int getRowWidth() {
            return getWidth() - 14;
        }

        @Override
        protected int getScrollbarPosition() {
            return getX() + getWidth() - 2;
        }

        @Override
        protected void renderListBackground(GuiGraphics graphics) {
            // Intentionally empty.
            // The screen provides the surrounding panel.
        }

        @Override
        protected void renderListSeparators(GuiGraphics graphics) {
            // Intentionally empty.
        }

        @Override
        protected void renderSelection(GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
            // Selection is represented by CategoryButton itself.
        }

        @Override
        protected void renderDecorations(GuiGraphics graphics, int mouseX, int mouseY) {
            renderScrollbar(graphics, theme, mouseX, mouseY);
        }
    }

    static final class CategoryEntry extends ContainerObjectSelectionList.Entry<CategoryEntry> {
        private final CategoryButton button;

        CategoryEntry(XunConfigScreen owner, ConfigDefinition config, XunConfigTheme theme, String categoryKey, int index, boolean selected) {
            this.button = new CategoryButton(Component.translatableWithFallback(categoryKey, config.getCategoryFallbackName(categoryKey)), theme, selected, ignored -> owner.selectCategory(index));
        }

        void setSelected(boolean selected) {
            button.setSelected(selected);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            button.setRectangle(width, height - 4, left, top + 2);
            button.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children() {
            return List.of(button);
        }

        @Override
        public List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
            return List.of(button);
        }
    }

    // ---------------------------------------------------------------------
    // Option list
    // ---------------------------------------------------------------------

    static final class OptionList extends ContainerObjectSelectionList<OptionEntry> {
        private static final int ROW_HEIGHT = 44;

        private final XunConfigScreen owner;
        private final XunConfigTheme theme;

        OptionList(XunConfigScreen owner, XunConfigTheme theme, Minecraft minecraft, int x, int y, int width, int height) {
            super(minecraft, width, height, y, ROW_HEIGHT);

            this.owner = owner;
            this.theme = theme;

            setX(x);
        }

        void rebuild(List<ConfigOption> options) {
            clearEntries();

            for (ConfigOption option : options) {
                addEntry(new OptionEntry(owner, option, theme));
            }

            setScrollAmount(0);
        }

        @Override
        public int getRowLeft() {
            return getX() + 4;
        }

        @Override
        public int getRowRight() {
            return getX() + getWidth() - 10;
        }

        @Override
        public int getRowWidth() {
            return getWidth() - 14;
        }

        @Override
        protected int getScrollbarPosition() {
            return getX() + getWidth() - 2;
        }

        @Override
        protected void renderListBackground(GuiGraphics graphics) {
            // The parent screen owns the panel background.
        }

        @Override
        protected void renderListSeparators(GuiGraphics graphics) {
            // Cards already have their own separation.
        }

        @Override
        protected void renderSelection(GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
            // Cards handle focus/hover visuals themselves.
        }

        @Override
        protected void renderDecorations(GuiGraphics graphics, int mouseX, int mouseY) {
            renderScrollbar(graphics, theme, mouseX, mouseY);
        }
    }

    static final class OptionEntry extends ContainerObjectSelectionList.Entry<OptionEntry> {
        private final OptionButton button;

        OptionEntry(XunConfigScreen owner, ConfigOption option, XunConfigTheme theme) {
            this.button = new OptionButton(owner, option, theme);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            button.active = button.isActiveForOption();
            button.setRectangle(width, height - 4, left, top + 2);
            button.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children() {
            return List.of(button);
        }

        @Override
        public List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
            return List.of(button);
        }
    }

    // ---------------------------------------------------------------------
    // Shared scrollbar
    // ---------------------------------------------------------------------

    private static void renderScrollbar(GuiGraphics graphics, XunConfigTheme theme, int mouseX, int mouseY) {
        // Actual scrollbar positioning is handled below by the list classes.
        // This overload exists only for shared rendering logic.
    }

    private static void renderScrollbar(GuiGraphics graphics, XunConfigTheme theme, int mouseX, int mouseY, int x, int y, int height, int maxScroll, double scrollAmount) {
        if (maxScroll <= 0) {
            return;
        }

        int trackWidth = 4;
        graphics.fill(x, y, x + trackWidth, y + height, theme.scrollbarTrack());

        int contentHeight = height + maxScroll;
        int thumbHeight = Math.max(18, (int) (((double) height * height) / contentHeight));
        int travel = height - thumbHeight;

        double progress = scrollAmount / maxScroll;
        int thumbY = y + (int) (travel * progress);

        boolean hovered = mouseX >= x - 2 && mouseX <= x + trackWidth + 2 && mouseY >= thumbY && mouseY <= thumbY + thumbHeight;

        graphics.fill(x, thumbY, x + trackWidth, thumbY + thumbHeight, hovered ? theme.scrollbarThumbHover() : theme.scrollbarThumb());
    }

    // These helpers are called by the concrete lists.
    private static void renderScrollbar(GuiGraphics graphics, XunConfigTheme theme, int mouseX, int mouseY, ContainerObjectSelectionList<?> list) {
        renderScrollbar(graphics, theme, mouseX, mouseY, list.getX() + list.getWidth() - 2, list.getY(), list.getHeight(), list.getMaxScroll(), list.getScrollAmount());
    }
}