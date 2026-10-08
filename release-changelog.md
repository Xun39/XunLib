## Xunlib v3.0.0

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