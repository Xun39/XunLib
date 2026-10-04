package net.xun.lib.common.api.config;

import net.minecraft.network.chat.Component;

public interface XunConfigOwner {
    ConfigOption getHoveredOption();

    void setHoveredOption(ConfigOption option);

    void selectCategory(int index);

    void selectConfigType(ConfigType type);

    Component getTitle();
}
