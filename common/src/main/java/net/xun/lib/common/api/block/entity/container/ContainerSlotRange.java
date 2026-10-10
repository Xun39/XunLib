package net.xun.lib.common.api.block.entity.container;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.IntStream;

public final class ContainerSlotRange implements Iterable<Integer> {
    private final Container container;
    private final int start;
    private final int end;

    private ContainerSlotRange(Container container, int start, int end) {
        this.container = Objects.requireNonNull(container, "container");
        this.start = start;
        this.end = end;
    }

    public static ContainerSlotRange of(Container container) {
        return ContainerSlotRange.of(container, 0, container.getContainerSize());
    }

    public static ContainerSlotRange of(Container container, int start, int end) {
        if (start > end) throw new IllegalArgumentException("Start must be <= end");
        if (start >= container.getContainerSize() || end >= container.getContainerSize()) {
            throw new IllegalArgumentException("Defined range is out of bound");
        }
        return new ContainerSlotRange(container, start, end);
    }

    public boolean contains(int slot) {
        return slot >= start && slot < end;
    }

    public Container getContainer() {
        return this.container;
    }

    public ItemStack getItem(int slot) {
        return container.getItem(slot);
    }

    public ItemStack getItemInRange(int slot) {
        if (!contains(slot)) {
            throw new IllegalArgumentException("The specified slot is within the range");
        }
        return container.getItem(slot);
    }

    public IntStream stream() {
        return IntStream.range(start, end);
    }

    @Override
    public @NotNull Iterator<Integer> iterator() {
        return stream().iterator();
    }
}
