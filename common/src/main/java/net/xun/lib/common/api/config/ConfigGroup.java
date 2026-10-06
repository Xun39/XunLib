package net.xun.lib.common.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a nested configuration group.
 *
 * <p>The group itself is not represented as a ConfigOption. Its fields are
 * recursively discovered and flattened into the owning ConfigDefinition.</p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigGroup {

    /**
     * Optional category override for all options in this group.
     *
     * <p>An empty value inherits the parent group's category.</p>
     */
    String category() default "";
}
