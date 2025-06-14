package net.xun.lib.common.internal.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.xun.lib.common.api.registries.Register;
import net.xun.lib.common.api.registries.RegistryHolder;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <T> Register<T> createRegister(ResourceKey<? extends Registry<T>> registry, String namespace);

    Register.Blocks createBlockRegister(String namespace);

    Register.Items createItemRegister(String namespace);

    /* <T extends Item> void bindItem(RegistryHolder<Item, T> holder, String namespace);

    <T extends Block> void bindBlock(RegistryHolder<Block, T> holder, String namespace); */
}