package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.components.IAreaWidget;
import net.xun.lib.common.api.client.gui.config.IThemedConfigGui;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.client.gui.config.layout.CategoryLayout;
import net.xun.lib.common.api.client.gui.config.layout.ChevronLayout;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigCategoryButton extends AbstractContainerWidget implements IAreaWidget, IThemedConfigGui {
    private final Component message;
    private final XunConfigTheme theme;

    private final int depth;
    private final boolean hasChildren;

    private final Runnable onSelect;
    private final Runnable onToggle;

    private boolean selected;
    private boolean expanded;

    private final CategoryLabelWidget labelWidget;

    public ConfigCategoryButton(Component message, XunConfigTheme theme, int depth, boolean hasChildren, boolean expanded, boolean selected, Runnable onSelect, Runnable onToggle) {
        super(0, 0, theme.layout().category().width(), theme.layout().category().height(), message);

        this.message = message;
        this.theme = theme;

        this.depth = Math.max(0, depth);
        this.hasChildren = hasChildren;

        this.selected = selected;
        this.expanded = expanded;

        this.onSelect = onSelect;
        this.onToggle = onToggle;

        this.labelWidget = new CategoryLabelWidget(message, theme);

        updateLabelState();
        updateLayout();
    }

    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        updateLabelState();
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public int depth() {
        return depth;
    }

    private void updateLabelState() {
        if (labelWidget == null) {
            return;
        }

        labelWidget.setSelected(selected);

        labelWidget.active = active;
        labelWidget.visible = true;
    }

    private Area arrowArea() {
        if (!hasChildren) {
            return Area.of(getX(), getY(), 0, 0);
        }

        CategoryLayout categoryLayout = layout().category();
        ChevronLayout chevronLayout = layout().chevron();

        int x = getX() + categoryLayout.textLeft() + depth * categoryLayout.childIndent();
        int y = getY() + (height - chevronLayout.size()) / 2;
        int size = chevronLayout.size() + chevronLayout.hitPadding() * 2;

        return Area.of(x - chevronLayout.hitPadding(), y - chevronLayout.hitPadding(), size, size);
    }

    private void updateLayout() {
        if (labelWidget == null) {
            return;
        }

        CategoryLayout categoryLayout = layout().category();
        ChevronLayout chevronLayout = layout().chevron();

        int contentWidth = Math.max(0, width - categoryLayout.textLeft() - categoryLayout.textRight() - depth * categoryLayout.childIndent() - chevronLayout.slotWidth());
        int contentHeight = Math.max(0, height - categoryLayout.textTop() - categoryLayout.textBottom());

        labelWidget.setSize(contentWidth, contentHeight);

        LinearLayout contentLayout = LinearLayout.horizontal();

        contentLayout.addChild(SpacerElement.width(depth * categoryLayout.childIndent() + chevronLayout.slotWidth()), LayoutSettings::alignVerticallyMiddle);
        contentLayout.addChild(labelWidget, LayoutSettings::alignVerticallyMiddle);

        contentLayout.setPosition(getX() + categoryLayout.textLeft(), getY() + categoryLayout.textTop());
        contentLayout.arrangeElements();
    }

    @Override
    public void setRectangle(int width, int height, int x, int y) {
        super.setRectangle(width, height, x, y);
        updateLayout();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        updateLayout();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        updateLayout();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        updateLayout();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        updateLayout();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != 0 || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (hasChildren && arrowArea().contains(mouseX, mouseY)) {
            expanded = !expanded;
            onToggle.run();

            Minecraft minecraft = Minecraft.getInstance();
            playDownSound(minecraft.getSoundManager());
            return true;
        }
        onSelect.run();

        Minecraft minecraft = Minecraft.getInstance();
        playDownSound(minecraft.getSoundManager());
        return true;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Area area = bounds();

        CategoryLayout categoryLayout = layout().category();
        ChevronLayout chevronLayout = layout().chevron();

        boolean hovered = active && area.contains(mouseX, mouseY);
        int background;

        if (selected) {
            background = theme.sidebar().selected();
        }
        else if (hovered) {
            background = theme.sidebar().hover();
        }
        else {
            background = theme.sidebar().background();
        }

        fill(graphics, area, background);

        if (selected) {
            drawLeftStrip(graphics, area, categoryLayout.selectedStripWidth(), categoryLayout.selectedStripInset(), theme.accent().primary());
        }

        if (hasChildren) {
            Area arrow = Area.of(
                    getX() + categoryLayout.textLeft() + depth * categoryLayout.childIndent(),
                    getY() + (height - chevronLayout.size()) / 2, chevronLayout.size(), chevronLayout.size()
            );
            drawChevron(graphics, arrow, expanded, hovered ? theme.text().primary() : theme.text().muted());
        }

        updateLabelState();

        labelWidget.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(labelWidget);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, message);

        if (hasChildren) {
            output.add(
                    NarratedElementType.USAGE,
                    Component.translatable(
                            expanded ? "gui.xunlib.config.category.collapse" : "gui.xunlib.config.category.expand"
                    )
            );
        }
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }

    private static final class CategoryLabelWidget extends AbstractWidget implements IThemedConfigGui {

        private final XunConfigTheme theme;

        private boolean selected;

        private CategoryLabelWidget(Component message, XunConfigTheme theme) {
            super(0, 0, 0, 0, message);

            this.theme = theme;
            this.active = false;
        }

        private void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int color = selected ? theme.text().primary() : theme.text().muted();
            AbstractWidget.renderScrollingString(graphics, Minecraft.getInstance().font, getMessage(), getX(), getY(), getX() + width, getY() + height, color);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        }

        @Override
        public XunConfigTheme theme() {
            return theme;
        }
    }
}