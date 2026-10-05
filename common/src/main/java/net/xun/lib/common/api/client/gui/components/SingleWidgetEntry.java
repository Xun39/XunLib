package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleWidgetEntry<E extends SingleWidgetEntry<E, W>, W extends AbstractWidget> extends ContainerObjectSelectionList.Entry<E> {
    protected final W widget;

    private final int verticalPadding;

    public SingleWidgetEntry(W widget, int verticalPadding) {
        this.widget = widget;
        this.verticalPadding = Math.max(0, verticalPadding);
    }

    public W getWidget() {
        return widget;
    }

    protected Area rowArea(int left, int top, int width, int height) {
        return Area.of(left, top + verticalPadding, width, Math.max(0, height - verticalPadding * 2));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
        Area row = rowArea(left, top, width, height);
        widget.setRectangle(row.width(), row.height(), row.x(), row.y());
        widget.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(widget);
    }

    @Override
    public @NotNull List<? extends NarratableEntry> narratables() {
        return List.of(widget);
    }
}