package net.xun.lib.common.api.inventory.slot;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SlotIterator implements Iterator<Integer> {

    private int current;
    private final int end;
    private final int step;

    public SlotIterator(int start, int end) {
        this.step = start <= end ? 1 : -1;
        this.current = start - step;
        this.end = end;
    }

    @Override
    public boolean hasNext() {
        if (step > 0) {
            return current + step < end;
        } else {
            return current + step > end;
        }
    }

    @Override
    public Integer next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        current += step;
        return current;
    }
}
