package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleWidgetEntry<W extends AbstractWidget> extends ContainerObjectSelectionList.Entry<SingleWidgetEntry<W>> {
    private final W widget;

    public SingleWidgetEntry(W widget) {
        this.widget = widget;
    }

    public W getWidget() {
        return widget;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
        widget.setRectangle(width, height - 4, left, top + 2);
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