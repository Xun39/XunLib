package net.xun.lib.common.api.inventory;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface ItemStackPredicate extends Predicate<ItemStack> {

    ItemStackPredicate IS_DAMAGED = stack -> !stack.isEmpty() && stack.isDamaged() && stack.isDamageableItem();
    ItemStackPredicate IS_FULL_STACK = stack -> !stack.isEmpty() && stack.getCount() >= stack.getMaxStackSize();
    ItemStackPredicate IS_EMPTY = ItemStack::isEmpty;

    /**
     * Creates a predicate matching items of specific class hierarchy
     * @param itemType Target class to match (e.g., SwordItem.class)
     * @return New predicate instance
     */
    static ItemStackPredicate ofType(Class<? extends Item> itemType) {
        Objects.requireNonNull(itemType, "Item type class cannot be null");
        return stack -> itemType.isInstance(stack.getItem());
    }

    static ItemStackPredicate allOf(List<ItemStackPredicate> predicates) {
        return stack -> predicates.stream().allMatch(p -> p.test(stack));
    }

    static ItemStackPredicate allOf(ItemStackPredicate... predicates) {
        return allOf(Arrays.asList(predicates));
    }

    static ItemStackPredicate allOf(ItemStackPredicate predicate1, ItemStackPredicate predicate2) {
        return stack -> predicate1.test(stack) && predicate2.test(stack);
    }

    static ItemStackPredicate anyOf(List<ItemStackPredicate> predicates) {
        return stack -> predicates.stream().anyMatch(p -> p.test(stack));
    }

    static ItemStackPredicate anyOf(ItemStackPredicate... predicates) {
        return anyOf(Arrays.asList(predicates));
    }

    static ItemStackPredicate anyOf(ItemStackPredicate predicate1, ItemStackPredicate predicate2) {
        return stack -> predicate1.test(stack) || predicate2.test(stack);
    }

    static ItemStackPredicate matchesItem(List<Item> items) {
        return stack -> stack != null && !stack.isEmpty() && items.stream().anyMatch(stack::is);
    }

    static ItemStackPredicate matchesItem(Item... items) {
        return stack -> stack != null && !stack.isEmpty() && Arrays.stream(items).anyMatch(stack::is);
    }

    static ItemStackPredicate matchesItem(Item item) {
        return stack -> stack != null && !stack.isEmpty() && stack.is(item);
    }

    static ItemStackPredicate matchesTag(TagKey<Item> itemTag) {
        return stack -> stack != null && !stack.isEmpty() && stack.is(itemTag);
    }

    default ItemStackPredicate and(ItemStackPredicate other) {
        return stack -> test(stack) && other.test(stack);
    }

    default ItemStackPredicate or(ItemStackPredicate other) {
        return stack -> test(stack) || other.test(stack);
    }

    static ItemStackPredicate not(ItemStackPredicate predicate) {
        return stack -> !predicate.test(stack);
    }
}
