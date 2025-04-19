package net.xun.lib.common.api.inventory.slot;

import net.minecraft.world.Container;

import java.util.Arrays;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

/**
 * Flexible slot range that can adapt to different container sizes.
 */
public class SlotRange {
    private final IntSupplier startSupplier;
    private final IntSupplier endSupplier;

    public SlotRange(IntSupplier startSupplier, IntSupplier endSupplier) {
        this.startSupplier = startSupplier;
        this.endSupplier = endSupplier;
    }

    public int getStart() {
        return startSupplier.getAsInt();
    }

    public int getEnd() {
        return endSupplier.getAsInt();
    }

    /**
     * Creates a fixed SlotRange from explicit values.
     *
     * @param start Starting slot (inclusive)
     * @param end Ending slot (exclusive)
     *
     * @throws IllegalArgumentException if start > end
     */
    public static SlotRange of(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("Start must be <= end");
        }
        return new SlotRange(() -> start, () -> end);
    }

    /**
     * Creates a single-slot range.
     */
    public static SlotRange single(int slot) {
        return new SlotRange(() -> slot, () -> slot + 1);
    }

    /**
     * Checks if a slot is within this range for a specific container.
     */
    public boolean contains(int slot, Container container) {
        final int containerSize = container.getContainerSize();
        final int safeStart = Math.min(startSupplier.getAsInt(), containerSize);
        final int safeEnd = Math.min(endSupplier.getAsInt(), containerSize);
        return slot >= safeStart && slot < safeEnd;
    }

    /**
     * Gets slots as a stream adjusted for container size.
     */
    public IntStream stream(Container container) {
        final int containerSize = container.getContainerSize();
        int rawStart = startSupplier.getAsInt();
        int rawEnd = endSupplier.getAsInt();

        int safeStart = Math.min(rawStart, containerSize);
        int safeEnd = Math.min(rawEnd, containerSize);

        // Handle inverted ranges
        if (safeStart > safeEnd) {
            int temp = safeStart;
            safeStart = safeEnd;
            safeEnd = temp;
        }

        return IntStream.range(safeStart, safeEnd);
    }

    /**
     * Combines multiple slot ranges into one.
     */
    public static SlotRange combine(SlotRange... ranges) {
        return new SlotRange(
                () -> Arrays.stream(ranges)
                        .mapToInt(r -> r.startSupplier.getAsInt())
                        .min()
                        .orElse(0),
                () -> Arrays.stream(ranges)
                        .mapToInt(r -> r.endSupplier.getAsInt())
                        .max()
                        .orElse(0)
        );
    }

    /**
     * Gets slots as an iterable sequence adjusted for container size.
     * <p>
     * Essential for backward compatibility and iterator-based workflows.
     *
     * @param container Target inventory container
     * @return Iterable slot sequence respecting container boundaries
     */
    public Iterable<Integer> getSlots(Container container) {
        final int containerSize = container.getContainerSize();
        final int rawStart = startSupplier.getAsInt();
        final int rawEnd = endSupplier.getAsInt();

        int safeStart = Math.max(0, Math.min(rawStart, containerSize));
        int safeEnd = Math.max(0, Math.min(rawEnd, containerSize));

        return () -> new SlotIterator(safeStart, safeEnd);
    }
}
