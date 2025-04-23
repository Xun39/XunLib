package net.xun.lib.common.api.exceptions;

import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.item.fuzzy.FuzzyMatcher;

/**
 * Exception thrown when invalid configuration combinations are detected in a
 * {@link FuzzyMatcher}. Typically, indicates conflicting matching rules that
 * cannot be resolved automatically.
 *
 * <h2>Common Causes</h2>
 * <ul>
 *   <li>Combining tag requirements with custom predicate rules</li>
 *   <li>Using attribute ignoring (durability/enchantments) with custom rules</li>
 *   <li>Conflicting component filter modes (whitelist + blacklist)</li>
 * </ul>
 *
 * @see FuzzyMatcher#matches(ItemStack, ItemStack)
 */
public class InvalidMatcherConfigurationException extends RuntimeException {
  public InvalidMatcherConfigurationException(String message) {
    super(message);
  }
}
