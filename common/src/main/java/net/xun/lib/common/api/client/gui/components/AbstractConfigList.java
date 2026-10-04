package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractConfigList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
    protected final XunConfigTheme theme;

    public AbstractConfigList(Minecraft minecraft, int width, int height, int y, int itemHeight, XunConfigTheme theme) {
        super(minecraft, width, height, y, itemHeight);
        this.theme = theme;
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

    // Suppress default Minecraft list decorations across all custom lists
    @Override
    protected void renderListBackground(@NotNull GuiGraphics graphics) {
    }

    @Override
    protected void renderListSeparators(@NotNull GuiGraphics graphics) {
    }

    @Override
    protected void renderSelection(@NotNull GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
    }
}