package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.util.Mth;
import net.xun.lib.common.api.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigTheme;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractScrollableConfigList<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractThemedConfigList<E> {
    private boolean draggingScrollbar;
    private double scrollbarGrabOffset;

    public AbstractScrollableConfigList(Minecraft minecraft, int width, int height, int y, int itemHeight, XunConfigTheme theme) {
        super(minecraft, width, height, y, itemHeight, theme);
    }

    private Area scrollbarTrackArea() {
        XunConfigLayout.List listLayout = layout().list();
        XunConfigLayout.Scrollbar scrollbarLayout = layout().scrollbar();

        int x = getX() + getWidth() - listLayout.scrollbarInset() - scrollbarLayout.width();
        return Area.of(x, getY(), scrollbarLayout.width(), getHeight());
    }

    private Area scrollbarThumbArea() {
        Area track = scrollbarTrackArea();

        int thumbHeight = getScrollbarThumbHeight();
        int travel = track.height() - thumbHeight;

        if (getMaxScroll() <= 0 || travel <= 0) {
            return Area.of(track.x(), track.y(), track.width(), thumbHeight);
        }

        double progress = getScrollAmount() / getMaxScroll();

        int thumbY = track.y() + (int) (travel * progress);
        return Area.of(track.x(), thumbY, track.width(), thumbHeight);
    }

    @Override
    protected int getScrollbarPosition() {
        return scrollbarTrackArea().x();
    }

    @Override
    protected void renderDecorations(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        renderCustomScrollbar(graphics, mouseX, mouseY);
    }

    private void renderCustomScrollbar(GuiGraphics graphics, int mouseX, int mouseY) {
        if (getMaxScroll() <= 0) {
            return;
        }
        Area track = scrollbarTrackArea();
        Area thumb = scrollbarThumbArea();
        Area hitArea = track.expandX(track.width());

        boolean hovered = draggingScrollbar || hitArea.contains(mouseX, mouseY);

        drawScrollbar(graphics, track, thumb, hovered);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && getMaxScroll() > 0) {
            Area track = scrollbarTrackArea();
            Area hitArea = track.expandX(track.width());

            if (hitArea.contains(mouseX, mouseY)) {
                Area thumb = scrollbarThumbArea();
                int travel = track.height() - thumb.height();

                // Check if Y coordinate aligns with the thumb (using the expanded hit area X bounds)
                if (mouseY >= thumb.y() && mouseY <= thumb.y() + thumb.height()) {
                    draggingScrollbar = true;
                    scrollbarGrabOffset = mouseY - thumb.y();
                }
                else if (travel > 0) {
                    // Track click: jump thumb center to cursor and initiate drag
                    double target = mouseY - track.y() - thumb.height() / 2.0;
                    double percentage = Mth.clamp(target / travel, 0.0, 1.0);

                    setScrollAmount(percentage * getMaxScroll());

                    draggingScrollbar = true;
                    scrollbarGrabOffset = thumb.height() / 2.0;
                }

                setFocused(null);
                setDragging(true); // Signal Minecraft's ContainerEventHandler
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar && button == 0) {
            Area track = scrollbarTrackArea();
            int thumbHeight = getScrollbarThumbHeight();
            int travel = track.height() - thumbHeight;

            if (travel <= 0) {
                setScrollAmount(0);
                return true;
            }

            double thumbTop = mouseY - track.y() - scrollbarGrabOffset;
            double percentage = Mth.clamp(thumbTop / travel, 0.0, 1.0);

            setScrollAmount(percentage * getMaxScroll());
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingScrollbar) {
            draggingScrollbar = false;
            setDragging(false);
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private int getScrollbarThumbHeight() {
        XunConfigLayout.Scrollbar scrollbarLayout = layout().scrollbar();

        int contentHeight = getHeight() + getMaxScroll();

        if (contentHeight <= 0) {
            return getHeight();
        }

        int calculatedHeight = (int) ((double) getHeight() * getHeight() / contentHeight);

        return Math.min(getHeight(), Math.max(scrollbarLayout.minThumbHeight(), calculatedHeight));
    }
}