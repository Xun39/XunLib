package net.xun.lib.common.api.item.fuzzy;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.xun.lib.common.api.inventory.predicates.InventoryPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Configuration container for {@link FuzzyMatcher} settings, defining comparison rules
 * and component filtering behavior. Instances are immutable and use a copy-on-write
 * pattern for fluent configuration.
 * <p>
 * This class provides granular control over:
 * <ul>
 *   <li>Durability/enchantment comparisons</li>
 *   <li>Count matching strategies</li>
 *   <li>Data component filtering modes</li>
 *   <li>Item tag requirements</li>
 *   <li>Custom validation rules</li>
 * </ul>
 *
 * @see FuzzyMatcher For the matcher using these configurations
 */
public class FuzzyConfig {

    /**
     * Enum defining count comparison strategies:
     * <ul>
     *   <li>{@link #IGNORE} - Disregards item counts entirely</li>
     *   <li>{@link #EXACT} - Requires identical stack sizes</li>
     *   <li>{@link #AT_LEAST} - Requires source count ≥ target count</li>
     * </ul>
     */
    public enum CountMode {
        /** Ignore item counts in comparisons */
        IGNORE,
        /** Require exact count matches */
        EXACT,
        /** Match if source has equal or greater count */
        AT_LEAST
    }

    /**
     * Enum defining filtering modes:
     * <ul>
     *   <li>{@link #WHITELIST} - Only compare specified objects</li>
     *   <li>{@link #BLACKLIST} - Compare all objects except specified</li>
     * </ul>
     */
    public enum FilterMode {
        /** Include only specified objects in comparison */
        WHITELIST,
        /** Exclude specified objects from comparison */
        BLACKLIST
    }

    boolean ignoreDurability = false;
    boolean ignoreEnchantments = false;
    CountMode countMode = CountMode.EXACT;
    Predicate<DataComponentType<?>> componentFilter = c -> true;
    TagKey<Item> requiredTag = null;
    List<InventoryPredicate> predicates = new ArrayList<>();

    /**
     * Sets whether durability values should be ignored in comparisons
     *
     * @param value true to disable durability checks, false for strict comparison
     * @return New configuration instance with updated setting
     */
    public FuzzyConfig withIgnoreDurability(boolean value) {
        FuzzyConfig copy = copy();
        copy.ignoreDurability = value;
        return copy;
    }

    /**
     * Sets whether enchantments should be ignored in comparisons
     *
     * @param value true to disable enchantment checks, false for strict comparison
     * @return New configuration instance with updated setting
     */
    public FuzzyConfig withIgnoreEnchantments(boolean value) {
        FuzzyConfig copy = copy();
        copy.ignoreEnchantments = value;
        return copy;
    }

    /**
     * Configures stack count comparison strategy
     *
     * @param countMode The count matching strategy to use
     * @return New configuration instance with updated strategy
     * @see CountMode
     */
    public FuzzyConfig withCountMode(CountMode countMode) {
        FuzzyConfig copy = copy();
        copy.countMode = countMode;
        return copy;
    }

    /**
     * Configures data component filtering using specified mode and components
     *
     * @param mode Filtering strategy to apply
     * @param components Set of components to whitelist/blacklist
     * @return New configuration instance with updated filter
     * @throws IllegalArgumentException If empty component set is provided with WHITELIST/BLACKLIST mode
     */
    public FuzzyConfig withComponentFilter(FilterMode mode, Set<DataComponentType<?>> components) {
        FuzzyConfig copy = copy();
        copy.componentFilter =
                mode == FilterMode.WHITELIST
                ? components::contains
                : c -> !components.contains(c);
        return copy;
    }

    /**
     * @deprecated
     * This method is deprecated, see {@link #withPredicateFilter(FilterMode, List)}
     * for new handling method
     */
    @Deprecated(since = "1.3")
    public FuzzyConfig addCustomRule(InventoryPredicate rule) {
        FuzzyConfig copy = copy();
        copy.predicates.add(rule);
        return copy;
    }

    /**
     * Configures custom predicate filtering using specified mode and predicates.
     *
     * @param mode Filtering strategy to apply (WHITELIST or BLACKLIST)
     * @param predicates List of predicates to include/exclude based on mode
     * @return New configuration instance with updated predicate rules
     * @throws IllegalArgumentException If empty predicate list is provided with WHITELIST/BLACKLIST mode
     */
    public FuzzyConfig withPredicateFilter(FilterMode mode, List<InventoryPredicate> predicates) {
        if (mode == FilterMode.WHITELIST || mode == FilterMode.BLACKLIST) {
            if (predicates == null || predicates.isEmpty()) {
                throw new IllegalArgumentException("Predicates cannot be empty for WHITELIST or BLACKLIST modes");
            }
        }

        FuzzyConfig copy = copy();

        if (mode == FilterMode.WHITELIST) {
            copy.predicates = new ArrayList<>(predicates);
        } else if (mode == FilterMode.BLACKLIST) {
            List<InventoryPredicate> negatedPredicates = new ArrayList<>(predicates.size());
            for (InventoryPredicate p : predicates) {
                negatedPredicates.add(item -> !p.test(item));
            }
            copy.predicates = negatedPredicates;
        } else {
            copy.predicates.clear();
        }
        return copy;
    }

    /**
     * Creates a deep copy of this configuration. Used internally
     * to maintain immutability when modifying settings.
     *
     * @return New independent configuration instance
     */
    public FuzzyConfig copy() {
        FuzzyConfig copy = new FuzzyConfig();
        copy.ignoreDurability = this.ignoreDurability;
        copy.ignoreEnchantments = this.ignoreEnchantments;
        copy.countMode = this.countMode;
        copy.componentFilter = this.componentFilter;
        copy.requiredTag = this.requiredTag;
        copy.predicates = new ArrayList<>(this.predicates);
        return copy;
    }
}
