package net.xun.lib.forge.api.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.xun.lib.common.api.exceptions.UtilityClassException;
import net.xun.lib.common.api.util.CommonUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ForgeRegistryHelper {

    private ForgeRegistryHelper() throws UtilityClassException {
        throw new UtilityClassException();
    }

    /**
     * Retrieves the Forge registry equivalent of a Minecraft registry.
     *
     * @param registry The Minecraft registry to convert
     * @return The corresponding Forge registry
     * @throws IllegalArgumentException if no matching Forge registry exists
     * @throws ClassCastException if the registry type doesn't match the expected type
     */
    @Nonnull
    public static <T> IForgeRegistry<T> getForgeRegistry(Registry<T> registry) {
        final ResourceLocation key = CommonUtils.getKey(registry);
        final IForgeRegistry<?> forgeRegistry = RegistryManager.ACTIVE.getRegistry(key);

        return castRegistry(registry, forgeRegistry, key);
    }

    /**
     * Safely retrieves the Forge registry equivalent of a Minecraft registry.
     *
     * @param registry The Minecraft registry to convert
     * @return Optional containing the Forge registry, or empty if not found
     */
    @Nonnull
    public static <T> Optional<IForgeRegistry<T>> getForgeRegistrySafe(Registry<T> registry) {
        try {
            return Optional.of(getForgeRegistry(registry));
        } catch (IllegalArgumentException | ClassCastException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> IForgeRegistry<T> castRegistry(Registry<T> vanillaRegistry, @Nullable IForgeRegistry<?> forgeRegistry, ResourceLocation key) {
        if (forgeRegistry == null) {
            throw new IllegalArgumentException(
                    "No Forge registry found for key: " + key + "\n" +
                            "This could be because:\n" +
                            "1. The registry isn't registered with Forge\n" +
                            "2. You're accessing it too early in the loading process\n" +
                            "3. The mod providing the registry isn't installed\n"
            );
        }

        if (!forgeRegistry.getRegistryName().equals(vanillaRegistry.key().registry())) {
            throw new ClassCastException(
                    "Registry type mismatch for key: " + key + "\n" +
                            "Expected: " + vanillaRegistry.key().registry().getPath() + "\n" +
                            "Actual: " + forgeRegistry.getRegistryName().getPath()
            );
        }

        return (IForgeRegistry<T>) forgeRegistry;
    }

    /**
     * Checks if a Forge registry exists for the given Minecraft registry.
     */
    public static boolean registryExists(Registry<?> registry) {
        return getForgeRegistrySafe(registry).isPresent();
    }
}
