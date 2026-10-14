package net.xun.lib.common.api.client.registration.context;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;

@FunctionalInterface
public interface ItemColorContext {
    void register(ItemColor itemColor, ItemLike... items);
}
