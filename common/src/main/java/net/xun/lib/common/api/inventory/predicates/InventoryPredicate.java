package net.xun.lib.common.api.inventory.predicates;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public interface InventoryPredicate extends Predicate<ItemStack> {

    InventoryPredicate IS_DAMAGED = stack -> stack != null && stack.isDamaged() && stack.isDamageableItem();
    InventoryPredicate IS_FULL_STACK = stack -> stack != null && !stack.isEmpty() && stack.getCount() >= stack.getMaxStackSize();
    InventoryPredicate IS_EMPTY = stack -> stack == null || stack.isEmpty();

    /**
     * Creates a predicate that matches items of the specified class or its subclasses.
     * <p>
     * Example: {@code InventoryPredicate.of(ArmorItem.class)}
     *
     * @param itemClass The class to check against (e.g., ArmorItem.class)
     * @return Predicate that matches items inheriting from {@code itemClass}
     */
    static InventoryPredicate of(Class<? extends Item> itemClass) {
        return stack -> stack != null
                && !stack.isEmpty()
                && itemClass.isInstance(stack.getItem());
    }

    static InventoryPredicate allOf(List<InventoryPredicate> predicates) {
        return stack -> predicates.stream().allMatch(p -> p.test(stack));
    }

    static InventoryPredicate allOf(InventoryPredicate... predicates) {
        return allOf(Arrays.asList(predicates));
    }

    static InventoryPredicate allOf(InventoryPredicate predicate1, InventoryPredicate predicate2) {
        return stack -> predicate1.test(stack) && predicate2.test(stack);
    }

    static InventoryPredicate anyOf(List<InventoryPredicate> predicates) {
        return stack -> predicates.stream().anyMatch(p -> p.test(stack));
    }

    static InventoryPredicate anyOf(InventoryPredicate... predicates) {
        return anyOf(Arrays.asList(predicates));
    }

    static InventoryPredicate anyOf(InventoryPredicate predicate1, InventoryPredicate predicate2) {
        return stack -> predicate1.test(stack) || predicate2.test(stack);
    }

    static InventoryPredicate matchesItem(List<Item> items) {
        return stack -> stack != null && !stack.isEmpty() && items.stream().anyMatch(stack::is);
    }

    static InventoryPredicate matchesItem(Item... items) {
        return stack -> stack != null && !stack.isEmpty() && Arrays.stream(items).anyMatch(stack::is);
    }

    static InventoryPredicate matchesItem(Item item) {
        return stack -> stack != null && !stack.isEmpty() && stack.is(item);
    }

    static InventoryPredicate matchesTag(TagKey<Item> itemTag) {
        return stack -> stack != null && !stack.isEmpty() && stack.is(itemTag);
    }

    static InventoryPredicate not(InventoryPredicate predicate) {
        return stack -> !predicate.test(stack);
    }
}
