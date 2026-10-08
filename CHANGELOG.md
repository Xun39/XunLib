# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog 1.1.0], and this project adheres to [Semantic Versioning 2.0.0].

## [3.0.0-1.21.1] - 2026-10-07

### Added

- Added the `Area` utility class for representing rectangular areas. It provides helpers for offsetting, insetting, expanding, and checking whether a point is contained within an area.
- Added `TerrainAwareJigsawStructure`.
- Added `BlockEntityTypeFactory`, which works like `BlockEntitySupplier` but allows `BlockEntityType` instances to be created in the common source set using XunLib's transformed access to `BlockEntitySupplier`.
- Added a complete configuration system. See the wiki for documentation and usage details.
- Added the `ArmorSet` and `ToolSet` APIs from the Armory API. See the Armory API changelog for additional details.

### Changed

- Renamed `TickingEntityBlock` to `ITickingEntityBlock`.
- Moved `InventoryPredicates` to `api.inventory` and renamed it to `ItemStackPredicate`.
- Moved color utilities from `misc.color` to `util.color`.
- Renamed `IColorBase` to `IColor`.
- Renamed `RGBColor` to `ARGBColor`.
- Renamed utility classes ending in `Utils` to `Util`.
- Split `CommonUtils` into `ResourceUtil` and `TranslationUtil`.
- Renamed `ArmorSlotsUtils` to `EquipmentSlotUtil`.
    - It now uses `net.minecraft.world.entity.EquipmentSlot` rather than fixed slot indexes.
- Renamed `MobEffectUtils` to `MobEffectUtil`.
    - Renamed `applySingleEffect` to `applyEffect`.
    - Renamed `applyEffectsWithStrategy` to `applyEffectWithStrategy`.
- Renamed `PlayerInventoryUtils` to `PlayerInvUtil`.
- Moved the `TakeOnlySlot` class from `api.inventory.slot` to `api.block.entity.container.slot`.
- Moved the entire registration API from `api.registries` to `api.registration`.
- Refactored `EffectStackingStrategy` into an interface with a single `apply(LivingEntity, MobEffectInstance, MobEffectInstance)` method.
- Added the `EffectStackingStrategies` class containing the built-in stacking strategies, allowing custom strategies to be implemented more easily.

### Removed

- Removed `api.inventory.slot.SlotGetter`.
- Removed the `api.item.fuzzy` package and its contents, including `FuzzyMatcher`, as the API was no longer considered useful.
- Removed the HSL color implementation.
- Removed `ColorCombiner`.
- Removed `hasEffect(LivingEntity, MobEffectInstance)` from `MobEffectUtil`.
- Removed `clearEffect` and `clearEffects` from `MobEffectUtil`, as they were only wrappers around single-line operations.
- Removed fixed-index slot handling from `ArmorSlotsUtils` in favor of `EquipmentSlot`.

## [2.1.5-1.21.1] - 2026-01-05

### Removed

- Removed the entire `@PersistentNbt` annotation system and its automatic NBT serialization/deserialization.

### Changed

- Replaced automatic NBT persistence with standard `CompoundTag` serialization in `saveAdditional()` and `loadAdditional()`.

### Fixed

- Fixed `combineAsNamespacedID` formatting so that it correctly inserts a colon (`:`) between the namespace and path.
    - Before: `namespacepath_part1_part2`
    - After: `namespace:path_part1_part2`

## [2.1.4-1.21.1] - 2025-06-16

### Added

- Added `getName()` to `ToolSet` and `ArmorSet`.
    - Returns the base material name, such as `ruby` for `Ruby Armor`.
    - Simplifies language JSON datagen.

- Added `hasEffect`, `clearEffect`, and `clearEffects` helpers to the mob effect utilities.

## [2.1.3-1.21.1] - 2025-06-13

### Added

- Added `UpgradeSmithingTemplateItem`, with Netherite upgrade behavior extracted from vanilla.
    - Supports customizing icons, description text, and material requirements.

### Changed

- Changed the `Item.Properties` field in the `ArmorSet` and `ToolSet` builders to a `Supplier` for safer delayed initialization.
- Updated `withVanillaBalance()` to correctly use Iron-tier attack damage and attack speed attributes.
- Restored the static `Registries` map in the common utilities to resolve `getKey()` and `getRegistryId()` issues.

## [2.1.0-1.21.1] - 2025-06-06

### Changed

- Renamed `Registrar` to `Register` to simplify the core registration API.
- Replaced lazy registry references with strongly typed registry holders:
    - `RegistryHolder` for generic registry objects.
    - `RegistryItem` for items.
    - `RegistryBlock` for blocks.
- Improved registration lifecycle handling and loader-agnostic initialization.
- Stabilized the registration flow to resolve issues introduced in earlier 1.6.x releases.

### Removed

- Removed Forge support due to incompatibilities with its registration system.
- Removed the legacy lazy registry references.

### Breaking Changes

- Mods using the old lazy registry API must migrate to the new `RegistryHolder`-based API.
- Forge is no longer supported.
- Fabric and NeoForge remain supported.

## [1.6.2-1.21.1] - 2025-05-09

### Changed

- Generalized `ItemRegistrar` into the public generic `Registrar<T>` class.

### Breaking Changes

- The registration API was marked as unstable and may require further changes in future releases.

## [1.5.1-1.21.1] - 2025-04-27

### Added

- Added AABB utilities to `BlockPosUtils`:
    - `createAABBFromCenter(BlockPos center, double xRadius, double yRadius, double zRadius)`
    - `expandAABB(AABB original, double x, double y, double z)`
    - `getUnionAABB(AABB a, AABB b)`
    - `isAABBWithinBlock(AABB box, BlockPos pos)`

### Fixed

- Fixed `ArmorSet` and `ToolSet` item initialization occurring too late, which could cause:
    - Registry freeze errors on Forge and NeoForge.
    - Intrusive holder creation crashes on Fabric.

### Changed

- Delayed item initialization in `ArmorSet` and `ToolSet` to prevent unsafe registry access during class loading.

## [1.5.0-1.21.1] - 2025-04-25

### Added

- Added a `Prevent Stacking` option to the effect stacking strategy.
- Added a fluent `MobEffectInstance` builder for creating and customizing effect instances.
- Added `ITickableBlockEntity` for block entities that require ticking updates.
- Added `TickingEntityBlock` as a base block class for simplified ticking block entity logic.
- Added the `ToolSet` builder for defining complete tool collections:
    - Sword
    - Axe
    - Pickaxe
    - Hoe
    - Shovel
- Added the `ArmorSet` builder for defining complete armor sets:
    - Helmet
    - Chestplate
    - Leggings
    - Boots

### Changed

- Moved `@PersistentNbt` from the `.nbt` package to `.annotations`.
- Moved all exceptions into the `.exceptions` package.
- Renamed `InventorySection` to `PlayerInventorySection`.
- Renamed `EffectUtils` to `MobEffectUtils`.
- Removed the `ALL` filter mode from fuzzy item matching configurations.

## [1.4.0-1.21.1] - 2025-04-21

### Added

- Added the `@PersistentNbt` annotation for automatically serializing and deserializing block entity data to and from NBT.
- Added `INbtAdapter` for custom NBT serialization and deserialization.
- Added whitelist and blacklist support for custom fuzzy-matching predicates.

### Changed

- Moved `setModId` from `CommonUtils` to `ModSetup`.
    - Mod initialization must now use `ModSetup.setModId(...)`.
- Renamed inventory methods:
    - `addItems(...)` → `insertItems(...)`
    - `removeItems(...)` → `extractItems(...)`

## [1.2.1-1.21.1] - 2025-04-19

### Added

- Improved the fuzzy matching system.
- Added additional color utilities.
- Added additional common utilities.
- Added additional block position utilities.

[Keep a Changelog 1.1.0]: https://keepachangelog.com/en/1.1.0/
[Semantic Versioning 2.0.0]: https://semver.org/spec/v2.0.0.html