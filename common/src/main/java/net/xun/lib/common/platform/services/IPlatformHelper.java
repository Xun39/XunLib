package net.xun.lib.common.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.xun.lib.common.api.registration.Register;

import java.nio.file.Path;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    Path getConfigDir();

    default Path getServerConfigOverrideDir(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve("serverconfig");
    }

    boolean isPhysicalClient();

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
}