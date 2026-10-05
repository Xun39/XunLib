package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.xun.lib.common.api.client.gui.IThemedConfigGui;
import net.xun.lib.common.api.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractThemedConfigList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> implements IThemedConfigGui {
    protected final XunConfigTheme theme;

    protected AbstractThemedConfigList(Minecraft minecraft, int width, int height, int y, int itemHeight, XunConfigTheme theme) {
        super(minecraft, width, height, y, itemHeight);
        this.theme = theme;
    }

    @Override
    public int getRowLeft() {
        XunConfigLayout.List metrics = layout().list();
        return getX() + metrics.rowLeftInset();
    }

    @Override
    public int getRowRight() {
        XunConfigLayout.List metrics = layout().list();
        return getX() + getWidth() - metrics.rowRightInset();
    }

    @Override
    public int getRowWidth() {
        XunConfigLayout.List metrics = layout().list();
        return metrics.rowWidth(getWidth());
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() + getWidth() - layout().list().scrollbarInset();
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