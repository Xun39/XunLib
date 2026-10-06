package net.xun.lib.common.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntry {
    /**
     * Translation key or raw fallback string for the option name
     */
    String name() default "";

    /**
     * Optional category override.
     *
     * <p>If empty, the category inherited from the containing
     * {@link ConfigGroup} is used.</p>
     */
    String category() default "";

    double min() default Double.NEGATIVE_INFINITY;

    double max() default Double.POSITIVE_INFINITY;

    double step() default 0.0;
}
