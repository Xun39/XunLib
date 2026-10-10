# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog 1.1.0], and this project adheres to [Semantic Versioning 2.0.0].

## [1.21.1-3.1.0] - 2026-10-09

### Changed

- Moved `ItemStackPredicate` from `api.inventory` to `api.item`.
- Refactored `SlotRange`, renamed it to `ContainerSlotRange`, and moved it to `api.block.entity.container`. See the relevant commit for details.
- Updated `InventoryUtil` methods that previously accepted `SlotRange` to use `ContainerSlotRange` instead.
- Renamed `InventoryUtil.insertItem()` to `tryInsertStack()` for a clearer description of its behavior.

### Deprecated

- Deprecated all APIs under `api.inventory` and marked them for removal.
- Deprecated `PlayerInvUtil` because most of its methods were simple one-line wrappers around existing functionality.

## [1.21.1-3.0.0] - 2026-10-07

### Added

- Added the `Area` utility class for representing rectangular areas. It provides helpers for offsetting, insetting, expanding, and checking whether a point is contained within an area.
- Added `TerrainAwareJigsawStructure`.
- Added `BlockEntityTypeFactory`, which works like `BlockEntitySupplier` but allows `BlockEntityType` instances to be created in the common source set using XunLib's transformed access to `BlockEntitySupplier`.
- Added a complete configuration system. See the wiki for documentation and usage details.
- Added the `ArmorSet` and `ToolSet` APIs from the Armory API. See the Armory API changelog for additional details.

### Changed

- Renamed `TickingEntityBlock` to `ITickingEntityBlock`.
- Moved `InventoryPredicates` from `api.inventory` to `api.inventory` and renamed it to `ItemStackPredicate`.
- Moved color utilities from `misc.color` to `util.color`.
- Renamed `IColorBase` to `IColor`.
- Renamed `RGBColor` to `ARGBColor`.
- Renamed utility classes ending in `Utils` to `Util`.
- Split `CommonUtils` into `ResourceUtil` and `TranslationUtil`.
- Renamed `ArmorSlotsUtils` to `EquipmentSlotUtil`.
    - Replaced fixed slot indexes with `net.minecraft.world.entity.EquipmentSlot`.
- Renamed `MobEffectUtils` to `MobEffectUtil`.
    - Renamed `applySingleEffect` to `applyEffect`.
    - Renamed `applyEffectsWithStrategy` to `applyEffectWithStrategy`.
- Renamed `PlayerInventoryUtils` to `PlayerInvUtil`.
- Moved `TakeOnlySlot` from `api.inventory.slot` to `api.block.entity.container.slot`.
- Moved the registration API from `api.registries` to `api.registration`.
- Refactored `EffectStackingStrategy` into an interface with a single `apply(LivingEntity, MobEffectInstance, MobEffectInstance)` method.
- Added `EffectStackingStrategies` to provide built-in stacking strategies while allowing custom implementations.

### Removed

- Removed `api.inventory.slot.SlotGetter`.
- Removed the `api.item.fuzzy` package and its contents, including `FuzzyMatcher`, as the API was no longer considered useful.
- Removed the HSL color implementation.
- Removed `ColorCombiner`.
- Removed `hasEffect(LivingEntity, MobEffectInstance)` from `MobEffectUtil`.
- Removed `clearEffect` and `clearEffects` from `MobEffectUtil`, as they were simple wrappers around single-line operations.

## [1.21.1-2.1.5] - 2026-01-05

### Removed

- Removed the entire `@PersistentNbt` annotation system and its automatic NBT serialization and deserialization.

### Changed

- Replaced automatic NBT persistence with standard `CompoundTag` serialization in `saveAdditional()` and `loadAdditional()`.

### Fixed

- Fixed `combineAsNamespacedID` to correctly insert a colon (`:`) between the namespace and path.
    - Before: `namespacepath_part1_part2`
    - After: `namespace:path_part1_part2`

## [1.21.1-2.1.4] - 2025-06-16

### Added

- Added `getName()` to `ToolSet` and `ArmorSet`.
    - Returns the base material name, such as `ruby` for `Ruby Armor`.
    - Simplifies language JSON datagen.
- Added mob effect utility methods for checking and clearing effects: `hasEffect`, `clearEffect`, and `clearEffects`.

## [1.21.1-2.1.3] - 2025-06-13

### Added

- Added `UpgradeSmithingTemplateItem`, with Netherite upgrade behavior extracted from vanilla.
    - Supports customizing icons, description text, and material requirements.

### Changed

- Changed the `Item.Properties` field in the `ArmorSet` and `ToolSet` builders to a `Supplier` for safer delayed initialization.
- Updated `withVanillaBalance()` to correctly use Iron-tier attack damage and attack speed attributes.
- Restored the static `Registries` map in the common utilities to resolve `getKey()` and `getRegistryId()` issues.

## [1.21.1-2.1.0] - 2025-06-06

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
- Removed legacy lazy registry references.

### Breaking Changes

- Mods using the old lazy registry API must migrate to the new `RegistryHolder`-based API.
- Forge is no longer supported. Fabric and NeoForge remain supported.

## [1.21.1-1.6.2] - 2025-05-09

### Changed

- Generalized `ItemRegistrar` into the public generic `Registrar<T>` class.

### Breaking Changes

- Marked the registration API as unstable and subject to further changes.

## [1.21.1-1.5.1] - 2025-04-27

### Added

- Added AABB utilities to `BlockPosUtils`:
    - `createAABBFromCenter(BlockPos center, double xRadius, double yRadius, double zRadius)`
    - `expandAABB(AABB original, double x, double y, double z)`
    - `getUnionAABB(AABB a, AABB b)`
    - `isAABBWithinBlock(AABB box, BlockPos pos)`

### Fixed

- Fixed `ArmorSet` and `ToolSet` item initialization occurring too late, which could cause registry freeze errors on Forge and NeoForge and intrusive holder creation crashes on Fabric.

### Changed

- Delayed item initialization in `ArmorSet` and `ToolSet` to prevent unsafe registry access during class loading.

## [1.21.1-1.5.0] - 2025-04-25

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

### Removed

- Removed the `ALL` filter mode from fuzzy item matching configurations.

## [1.21.1-1.4.0] - 2025-04-21

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

## [1.21.1-1.2.1] - 2025-04-19

### Added

- Improved the fuzzy matching system.
- Added additional color utilities.
- Added additional common utilities.
- Added additional block position utilities.

[Keep a Changelog 1.1.0]: https://keepachangelog.com/en/1.1.0/
[Semantic Versioning 2.0.0]: https://semver.org/spec/v2.0.0.html

[1.21.1-3.1.0]: https://github.com/Xun39/XunLib/releases/tag/1.21.1-3.1.0
[1.21.1-3.0.0]: https://github.com/Xun39/XunLib/releases/tag/v3.0.0