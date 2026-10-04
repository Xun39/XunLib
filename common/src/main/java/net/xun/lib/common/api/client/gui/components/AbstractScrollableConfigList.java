package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.util.Mth;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractScrollableConfigList<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractConfigList<E> {
    private boolean draggingScrollbar;
    private double scrollbarGrabOffset;

    public AbstractScrollableConfigList(Minecraft minecraft, int width, int height, int y, int itemHeight, XunConfigTheme theme) {
        super(minecraft, width, height, y, itemHeight, theme);
    }

    @Override protected void renderDecorations(@NotNull GuiGraphics g, int mx, int my) {
        renderCustomScrollbar(g, mx, my);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && getMaxScroll() > 0) {
            int scrollbarX = getScrollbarPosition();

            if (mouseX >= scrollbarX - 5 && mouseX <= scrollbarX + 5 && mouseY >= getY() && mouseY <= getBottom()) {

                int thumbHeight = getScrollbarThumbHeight();

                int thumbY = getScrollbarThumbY();

                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {

                    draggingScrollbar = true;
                    scrollbarGrabOffset = mouseY - thumbY;

                    setFocused(null);

                    return true;
                }

                // Clicking the track moves the thumb toward the click.
                double position = (mouseY - getY()) / (double) getHeight();

                setScrollAmount(position * getMaxScroll());

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar && button == 0) {
            int thumbHeight = getScrollbarThumbHeight();

            int travel = getHeight() - thumbHeight;

            if (travel <= 0) {
                setScrollAmount(0);
                return true;
            }

            double thumbTop = mouseY - getY() - scrollbarGrabOffset;

            double percentage = thumbTop / travel;

            percentage = Mth.clamp(percentage, 0.0, 1.0);

            setScrollAmount(percentage * getMaxScroll());

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingScrollbar) {
            draggingScrollbar = false;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private int getScrollbarThumbHeight() {
        int contentHeight = getHeight() + getMaxScroll();

        return Math.max(18, (int) ((double) getHeight() * getHeight() / contentHeight));
    }

    private int getScrollbarThumbY() {
        int thumbHeight = getScrollbarThumbHeight();

        int travel = getHeight() - thumbHeight;

        if (getMaxScroll() <= 0) {
            return getY();
        }

        double progress = getScrollAmount() / getMaxScroll();

        return getY() + (int) (travel * progress);
    }

    private void renderCustomScrollbar(GuiGraphics graphics, int mouseX, int mouseY) {
        if (getMaxScroll() <= 0) {
            return;
        }

        int x = getScrollbarPosition();
        int y = getY();
        int height = getHeight();

        int thumbHeight = getScrollbarThumbHeight();

        int thumbY = getScrollbarThumbY();

        // Track
        graphics.fill(x, y, x + 4, y + height, theme.scrollbarTrack());

        boolean hovered = mouseX >= x - 3 && mouseX <= x + 7 && mouseY >= thumbY && mouseY <= thumbY + thumbHeight;

        // Thumb
        graphics.fill(x, thumbY, x + 4, thumbY + thumbHeight, draggingScrollbar ? theme.scrollbarThumbHover() : hovered ? theme.scrollbarThumbHover() : theme.scrollbarThumb());
    }
}
