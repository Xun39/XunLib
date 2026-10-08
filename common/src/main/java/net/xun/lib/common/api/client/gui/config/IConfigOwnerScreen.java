package net.xun.lib.common.api.client.gui.config;

import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.ConfigType;

public interface IConfigOwnerScreen {
    ConfigOption getHoveredOption();

    void setHoveredOption(ConfigOption option);

    void selectCategory(String categoryKey);

    void selectConfigType(ConfigType type);

    Component getTitle();
}
