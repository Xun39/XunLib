package net.xun.lib.common.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.xun.lib.common.impl.item.PieceType;
import net.xun.lib.common.impl.util.LazyReference;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A generic container for a collection of related {@link Item} instances,
 * where each item is identified by a piece type.
 * <p>
 * An {@code ItemSet} associates each {@link PieceType} with a lazily initialized
 * item reference and an item factory used during registration. Items are not
 * created until their corresponding supplier is resolved, allowing item sets
 * to be declared before the actual registry instances are available.
 * </p>
 * <p>
 * Each piece is assigned a registry name derived from the set name and the
 * piece's registry suffix. These names can be used to generate registration
 * entries and to bind the resulting registered item instances back to the set.
 * </p>
 * <p>
 * This class is intended to serve as a common abstraction for specialized
 * item-set implementations such as {@link net.xun.lib.common.api.item.tools.ToolSet}
 * and {@link net.xun.lib.common.api.item.armor.ArmorSet}.
 * </p>
 *
 * @param <P> the piece type used to identify individual items in the set
 * @param <T> the common item type represented by this set
 * @since 2.0.0
 */
public class ItemSet<P extends PieceType, T extends Item> {

    /**
     * The base name of this item set, used for generating registry IDs.
     */
    protected final String setName;

    private final LinkedHashMap<P, LazyReference<T>> pieces = new LinkedHashMap<>();
    private final Map<String, LazyReference<T>> piecesByRegistryName = new LinkedHashMap<>();
    private final LinkedHashMap<P, Function<Item.Properties, T>> factories = new LinkedHashMap<>();

    /**
     * Constructs an item set with the specified name, piece types, and item factory.
     * <p>
     * Each supplied piece is registered internally with a lazy reference and a
     * factory capable of creating the corresponding item from
     * {@link Item.Properties}. The supplied collection is copied into the internal
     * data structures, so subsequent modifications to the collection do not affect
     * this set.
     * </p>
     *
     * @param setName    the base name used to generate registry names
     * @param pieceTypes the piece types belonging to this set; must not be empty
     * @param factory    the factory used to create an item for each piece
     * @throws NullPointerException     if {@code setName}, {@code pieceTypes},
     *                                  {@code factory}, or a piece is {@code null}
     * @throws IllegalArgumentException if {@code pieceTypes} is empty
     */
    protected ItemSet(String setName, Collection<P> pieceTypes, BiFunction<P, Item.Properties, T> factory) {
        this.setName = Objects.requireNonNull(setName, "setName");

        Objects.requireNonNull(pieceTypes, "pieceTypes");
        Objects.requireNonNull(factory, "factory");

        if (pieceTypes.isEmpty()) {
            throw new IllegalArgumentException("pieceTypes cannot be empty");
        }

        for (P piece : pieceTypes) {
            Objects.requireNonNull(piece, "piece");

            String registryName = piece.registryName(setName);
            LazyReference<T> reference = new LazyReference<>(registryName);

            pieces.put(piece, reference);
            piecesByRegistryName.put(registryName, reference);
            factories.put(piece, properties -> factory.apply(piece, properties));
        }
    }

    /**
     * Generates the registry entries for all pieces in this set.
     * <p>
     * Each entry maps a {@link ResourceLocation} constructed from the supplied
     * namespace and the piece's generated registry name to a factory accepting
     * {@link Item.Properties}. The returned factories create the actual item
     * instances when invoked by the registry system.
     * </p>
     * <p>
     * The registry names are generated when the set is constructed and therefore
     * remain consistent across registration and later item access.
     * </p>
     *
     * @param modId the namespace used for the generated resource locations
     * @return an insertion-ordered map containing one registration entry for
     * each piece in this set
     * @throws NullPointerException     if {@code modId} is {@code null}
     * @throws IllegalArgumentException if {@code modId} is not a valid resource
     *                                  location namespace
     * @see ResourceLocation#fromNamespaceAndPath(String, String)
     */
    public Map<ResourceLocation, Function<Item.Properties, T>> getPiecesForRegistration(String modId) {
        Map<ResourceLocation, Function<Item.Properties, T>> registryEntries = new LinkedHashMap<>();

        for (Map.Entry<P, Function<Item.Properties, T>> entry : factories.entrySet()) {
            P piece = entry.getKey();
            String registryName = pieces.get(piece).getName();

            registryEntries.put(
                    ResourceLocation.fromNamespaceAndPath(modId, registryName),
                    entry.getValue()
            );
        }

        return registryEntries;
    }

    /**
     * Binds a supplier to the lazy reference associated with a registry name.
     * <p>
     * This method is intended for use by the registration layer after an item
     * instance has been registered. The supplied value is subsequently resolved
     * when the corresponding piece is accessed through {@link #get(P)} or
     * {@link #getAll()}.
     * </p>
     *
     * @param registryName the registry name of the piece to bind
     * @param supplier     the supplier used to resolve the registered item
     * @throws NullPointerException     if {@code supplier} is {@code null}
     * @throws IllegalArgumentException if {@code registryName} does not identify
     *                                  a piece in this set
     * @see LazyReference#bind(Supplier)
     */
    public void bind(String registryName, Supplier<T> supplier) {
        LazyReference<T> reference = piecesByRegistryName.get(registryName);
        if (reference == null) {
            throw new IllegalArgumentException("Unknown registry name '" + registryName + "' for set '" + setName + "'");
        }
        reference.bind(supplier);
    }

    /**
     * Returns the lazy reference associated with the specified piece.
     * <p>
     * The returned reference implements {@link Supplier}, allowing the item to be
     * resolved when required rather than requiring the item instance to be
     * created at the time the set itself is constructed.
     * </p>
     *
     * @param piece the piece whose item reference should be returned
     * @return the lazy reference for the specified piece
     * @throws NullPointerException     if {@code piece} is {@code null}
     * @throws IllegalArgumentException if {@code piece} is not part of this set
     */
    public Supplier<T> get(P piece) {
        LazyReference<T> reference = pieces.get(piece);
        if (reference == null) {
            throw new IllegalArgumentException("Unknown piece '" + piece + "' for set '" + setName + "'");
        }
        return pieces.get(piece);
    }

    /**
     * Resolves and returns all items belonging to this set.
     * <p>
     * Calling this method causes every item's lazy reference to be resolved.
     * Consequently, this method should generally be used when the complete set
     * of item instances is required rather than when only an individual piece
     * is needed.
     * </p>
     * <p>
     * The returned collection follows the insertion order of the pieces supplied
     * when this set was constructed.
     * </p>
     *
     * @return an unmodifiable list containing every item in this set
     */
    public Collection<T> getAll() {
        return pieces.values().stream().map(Supplier::get).toList();
    }

    /**
     * Returns the piece types belonging to this set.
     * <p>
     * The returned set is backed by the set's internal piece map and cannot be
     * modified through the returned view.
     * </p>
     *
     * @return an unmodifiable set of the pieces in this set
     */
    public Set<P> getPieces() {
        return Collections.unmodifiableSet(pieces.keySet());
    }


    /**
     * Returns the base name used to generate registry names for this set.
     *
     * @return the set's base registry name
     */
    public String getSetName() {
        return setName;
    }
}
