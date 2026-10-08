package net.xun.lib.common.api.client.gui.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.xun.lib.common.api.client.gui.config.layout.DecorationLayout;
import net.xun.lib.common.api.client.gui.config.layout.SliderLayout;
import net.xun.lib.common.api.client.gui.config.layout.ToggleLayout;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

public interface IThemedConfigGui {
    XunConfigTheme theme();

    default XunConfigLayout layout() {
        return theme().layout();
    }

    default void fill(@NotNull GuiGraphics graphics, @NotNull Area area, int color) {
        if (area.isEmpty()) return;
        graphics.fill(area.x1(), area.y1(), area.x2(), area.y2(), color);
    }

    default void drawBorder(@NotNull GuiGraphics graphics, @NotNull Area area, int color) {
        if (area.isEmpty()) return;
        int thickness = layout().decoration().borderWidth();

        fill(graphics, area.topEdge(thickness), color);
        fill(graphics, area.bottomEdge(thickness), color);
        fill(graphics, area.leftEdge(thickness), color);
        fill(graphics, area.rightEdge(thickness), color);
    }

    default void drawShadow(@NotNull GuiGraphics graphics, @NotNull Area area) {
        DecorationLayout decoration = layout().decoration();
        fill(graphics, area.offset(decoration.shadowDx(), decoration.shadowDy()), theme().panel().shadow());
    }

    default void drawCard(@NotNull GuiGraphics graphics, @NotNull Area area, boolean enabled, boolean hovered) {
        XunConfigTheme theme = theme();

        int background = !enabled ? theme.card().disabled() : hovered ? theme.card().hover() : theme.card().background();
        int border = !enabled ? theme.card().disabledBorder() : hovered ? theme.accent().primary() : theme.card().border();

        drawShadow(graphics, area);
        fill(graphics, area, background);
        drawBorder(graphics, area, border);

        if (hovered) {
            DecorationLayout decoration = layout().decoration();
            drawLeftStrip(graphics, area, decoration.accentStripWidth(), decoration.accentStripInset(), theme.accent().primary());
        }
    }

    default void drawLeftStrip(@NotNull GuiGraphics graphics, @NotNull Area area, int width, int inset, int color) {
        fill(graphics, area.insetY(inset).withLeftEdge(width), color);
    }

    default void drawRightStrip(@NotNull GuiGraphics graphics, @NotNull Area area, int width, int inset, int color) {
        fill(graphics, area.insetY(inset).withRightEdge(width), color);
    }

    default void drawChevron(@NotNull GuiGraphics graphics, @NotNull Area area, boolean expanded, int color) {
        if (area.isEmpty()) {
            return;
        }

        int centerX = area.centerX();
        int centerY = area.centerY();

        int size = Math.max(2, Math.min(area.width(), area.height()) / 2);

        if (expanded) {
            // Down chevron
            for (int i = 0; i < size; i++) {
                graphics.fill(centerX - size + i, centerY - size / 2 + i, centerX - size + i + 1, centerY - size / 2 + i + 1, color);
                graphics.fill(centerX + size - i - 1, centerY - size / 2 + i, centerX + size - i, centerY - size / 2 + i + 1, color);
            }
        }
        else {
            // Right chevron
            for (int i = 0; i < size; i++) {
                graphics.fill(centerX - size / 2 + i, centerY - size + i, centerX - size / 2 + i + 1, centerY - size + i + 1, color);
                graphics.fill(centerX + size / 2 - i - 1, centerY + i, centerX + size / 2 - i, centerY + i + 1, color);
            }
        }
    }

    default void drawToggle(@NotNull GuiGraphics graphics, @NotNull Area area, boolean on, boolean enabled) {
        if (area.isEmpty()) return;

        XunConfigTheme theme = theme();
        ToggleLayout toggle = layout().toggle();

        int track = !enabled ? theme.card().disabled() : on ? theme.toggle().on() : theme.toggle().off();
        fill(graphics, area, track);

        if (enabled && on) {
            fill(graphics, area.topEdge(toggle.highlightHeight()), theme.toggle().highlight());
        }

        int knobSize = Math.max(toggle.minKnobSize(), area.height() - toggle.knobInset() * 2);
        int knobX = on ? area.x2() - toggle.knobInset() - knobSize : area.x1() + toggle.knobInset();
        int knobY = area.y1() + toggle.knobInset();
        Area knob = Area.of(knobX, knobY, knobSize, knobSize);

        fill(graphics, knob.offset(toggle.knobShadowOffset(), toggle.knobShadowOffset()), theme.toggle().knobShadow());
        fill(graphics, knob, enabled ? theme.toggle().knob() : theme.text().disabled());
    }

    default void drawSlider(@NotNull GuiGraphics graphics, @NotNull Area area, double fraction, boolean enabled) {
        if (area.isEmpty()) {
            return;
        }

        XunConfigTheme theme = theme();
        SliderLayout slider = layout().slider();

        double value = Math.clamp(fraction, 0.0, 1.0);

        fill(graphics, area, enabled ? theme.toggle().off() : theme.card().disabled());
        int filled = (int) (area.width() * value);

        if (filled > 0) {
            fill(graphics, area.withWidth(filled), enabled ? theme.accent().primary() : theme.text().disabled());
        }

        int handleWidth = Math.max(slider.handleMinWidth(), area.height() / 2);
        int handleX = area.x() + (int) ((area.width() - handleWidth) * value);
        Area handle = Area.of(handleX, area.y(), handleWidth, area.height()).expandY(slider.handlePadding());

        fill(graphics, handle, enabled ? theme.toggle().knob() : theme.text().disabled());
    }

    default void drawScrollbar(@NotNull GuiGraphics graphics, @NotNull Area track, @NotNull Area thumb, boolean hovered) {
        XunConfigTheme theme = theme();
        fill(graphics, track, theme.scrollbar().track());
        fill(graphics, thumb, hovered ? theme.scrollbar().thumbHover() : theme.scrollbar().thumb());
    }

    default void drawGradientPanel(@NotNull GuiGraphics graphics, @NotNull Area area, int topColor, int bottomColor, int borderColor) {
        if (area.isEmpty()) return;
        graphics.fillGradient(area.x1(), area.y1(), area.x2(), area.y2(), topColor, bottomColor);
        drawBorder(graphics, area, borderColor);
    }

    default String truncate(@NotNull Font font, @NotNull String text, int maxWidth) {
        if (maxWidth <= 0 || text.isEmpty()) {
            return "";
        }

        if (font.width(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";

        int ellipsisWidth = font.width(ellipsis);
        int budget = Math.max(0, maxWidth - ellipsisWidth);

        return font.plainSubstrByWidth(text, budget) + ellipsis;
    }

    default void drawRightAligned(@NotNull GuiGraphics graphics, @NotNull Font font, @NotNull String text, int rightEdge, int y, int color, int maxWidth) {
        String value = maxWidth > 0 ? truncate(font, text, maxWidth) : text;
        graphics.drawString(font, value, rightEdge - font.width(value), y, color, false);
    }
}