package net.xun.lib.common.api.item.tools;

/**
 * Runtime information associated with a concrete Armory-created tool item.
 *
 * @param piece      the type of tool piece represented by the item
 * @param context    the context used to create the item
 * @param customizer the customizer responsible for creating/customizing the item
 * @since 3.0.0
 */
public record ToolMetaData(
        ToolPieceType piece,
        ToolContext context,
        ToolCustomizer customizer
) {
}
