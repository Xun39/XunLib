package net.xun.lib.common.api.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public final class GuiDrawUtil {
    private GuiDrawUtil() {
    }

    /* ------------------------------------------------------------------ */
    /*  Rectangle primitives                                              */
    /* ------------------------------------------------------------------ */

    public static void border(@NotNull GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        if (x2 <= x1 || y2 <= y1) return;

        // top / bottom
        g.fill(x1, y1, x2, y1 + 1, color);
        g.fill(x1, y2 - 1, x2, y2, color);

        // left / right (avoid double-drawing the corners)
        g.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
        g.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    public static void bevel(@NotNull GuiGraphics g, int x1, int y1, int x2, int y2, int light, int dark) {
        if (x2 <= x1 || y2 <= y1) return;

        // light: top + left
        g.fill(x1, y1, x2, y1 + 1, light);
        g.fill(x1, y1 + 1, x1 + 1, y2, light);

        // dark: bottom + right
        g.fill(x1, y2 - 1, x2, y2, dark);
        g.fill(x2 - 1, y1 + 1, x2, y2 - 1, dark);
    }

    public static void dropShadow(@NotNull GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        if (x2 <= x1 || y2 <= y1) return;
        g.fill(x1 + 1, y1 + 2, x2 + 1, y2 + 1, color);
    }

    public static void leftStrip(@NotNull GuiGraphics g, int x, int y1, int y2, int width, int vInset, int color) {
        if (width <= 0 || y2 - y1 <= vInset * 2) return;
        g.fill(x, y1 + vInset, x + width, y2 - vInset, color);
    }

    public static void rightStrip(@NotNull GuiGraphics g, int x, int y1, int y2, int width, int vInset, int color) {
        if (width <= 0 || y2 - y1 <= vInset * 2) return;
        g.fill(x - width, y1 + vInset, x, y2 - vInset, color);
    }

    public static void bottomStrip(@NotNull GuiGraphics g, int x1, int y, int x2, int height, int hInset, int color) {
        if (height <= 0 || x2 - x1 <= hInset * 2) return;
        g.fill(x1 + hInset, y - height, x2 - hInset, y, color);
    }

    public static void topStrip(@NotNull GuiGraphics g, int x1, int y, int x2, int height, int hInset, int color) {
        if (height <= 0 || x2 - x1 <= hInset * 2) return;
        g.fill(x1 + hInset, y, x2 - hInset, y + height, color);
    }

    @NotNull
    public static String truncate(@NotNull Font font, @NotNull String text, int maxWidth) {
        if (maxWidth <= 0 || text.isEmpty()) return "";
        if (font.width(text) <= maxWidth) return text;

        // Reserve room for the ellipsis.
        int ellipsisWidth = font.width("...");
        int budget = Math.max(0, maxWidth - ellipsisWidth);

        String shortened = font.plainSubstrByWidth(text, budget);
        return shortened + "...";
    }

    public static void drawRightAligned(@NotNull GuiGraphics g, @NotNull Font font, @NotNull String text, int rightEdge, int y, int color, int maxWidth) {
        String finalText = maxWidth > 0 ? truncate(font, text, maxWidth) : text;
        int width = font.width(finalText);
        g.drawString(font, finalText, rightEdge - width, y, color, false);
    }

    public static void drawCentered(@NotNull GuiGraphics g, @NotNull Font font, @NotNull String text, int left, int right, int y, int color) {
        int width = font.width(text);
        int x = left + (right - left - width) / 2;
        g.drawString(font, text, x, y, color, false);
    }

    public static void toggle(@NotNull GuiGraphics g, int x, int y, int width, int height, boolean on, boolean enabled, @NotNull XunConfigTheme theme) {
        if (width <= 0 || height <= 0) return;

        int trackColor = !enabled ? theme.cardDisabled() : on ? theme.toggleOn() : theme.toggleOff();

        g.fill(x, y, x + width, y + height, trackColor);

        // Highlight the top edge of an "on" track.
        if (enabled && on) {
            g.fill(x, y, x + width, y + 1, 0x40FFFFFF);
        }

        int knobSize = Math.max(2, height - 4);
        int knobX = on ? x + width - knobSize - 2 : x + 2;
        int knobY = y + 2;

        // Knob shadow
        g.fill(knobX + 1, knobY + 1, knobX + knobSize - 1, y + height - 1, 0x50000000);

        // Knob body
        int knobColor = enabled ? theme.toggleKnob() : theme.textDisabled();
        g.fill(knobX, knobY, knobX + knobSize, y + height - 2, knobColor);
    }

    public static void slider(@NotNull GuiGraphics g, int x, int y, int width, int height, double fraction, boolean enabled, @NotNull XunConfigTheme theme) {
        if (width <= 0 || height <= 0) return;

        double clamped = Math.max(0.0, Math.min(1.0, fraction));

        // Track
        g.fill(x, y, x + width, y + height, enabled ? theme.toggleOff() : theme.cardDisabled());

        // Filled portion
        int filled = (int) (width * clamped);
        if (filled > 0) {
            g.fill(x, y, x + filled, y + height, enabled ? theme.accent() : theme.textDisabled());
        }

        // Handle
        int handleW = Math.max(4, height / 2);
        int handleX = x + (int) ((width - handleW) * clamped);
        g.fill(handleX, y - 1, handleX + handleW, y + height + 1, enabled ? theme.toggleKnob() : theme.textDisabled());
    }

    public static void checkMark(@NotNull GuiGraphics g, int x, int y, int size, int color) {
        if (size < 3) return;

        int third = Math.max(1, size / 3);
        int mid = y + size - third;

        // Down-stroke of the check (left half)
        for (int i = 0; i < third; i++) {
            g.fill(x + i, mid + i, x + i + 1, mid + i + 1, color);
        }
        // Up-stroke (right half)
        for (int i = 0; i < size - third; i++) {
            int px = x + third + i;
            int py = mid + third - i;
            g.fill(px, py, px + 1, py + 1, color);
        }
    }

    public static void gradientPanel(@NotNull GuiGraphics g, int x1, int y1, int x2, int y2, int topColor, int bottomColor, int borderColor) {
        if (x2 <= x1 || y2 <= y1) return;

        g.fillGradient(x1, y1, x2, y2, topColor, bottomColor);
        border(g, x1, y1, x2, y2, borderColor);
    }
}