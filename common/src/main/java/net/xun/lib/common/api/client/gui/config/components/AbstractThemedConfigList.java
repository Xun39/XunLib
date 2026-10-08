package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.xun.lib.common.api.client.gui.config.IThemedConfigGui;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.client.gui.config.layout.ListLayout;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractThemedConfigList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> implements IThemedConfigGui {
    protected final XunConfigTheme theme;

    protected AbstractThemedConfigList(Minecraft minecraft, int width, int height, int y, int itemHeight, XunConfigTheme theme) {
        super(minecraft, width, height, y, itemHeight);
        this.theme = theme;
    }

    @Override
    public int getRowLeft() {
        ListLayout metrics = layout().list();
        return getX() + metrics.rowLeftInset();
    }

    @Override
    public int getRowRight() {
        ListLayout metrics = layout().list();
        return getX() + getWidth() - metrics.rowRightInset();
    }

    @Override
    public int getRowWidth() {
        ListLayout metrics = layout().list();
        return metrics.rowWidth(getWidth());
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() + getWidth() - layout().list().scrollbarInset() - SCROLLBAR_WIDTH;
    }

    @Override
    protected void renderListBackground(@NotNull GuiGraphics graphics) {
    }

    @Override
    protected void renderListSeparators(@NotNull GuiGraphics graphics) {
    }

    @Override
    protected void renderSelection(@NotNull GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }
}