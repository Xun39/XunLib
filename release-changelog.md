## XunLib 3.2.0

### Added
- Added `RegistryContainer`, a namespace-scoped registration container that provides access to a `Registrar<Registry>` for any registry.
- Added convenience methods to `RegistryContainer` for direct registration: `register()`, `registerItem()`, and `registerBlock()`.

### Changed
- Reworked the registration backend and introduced the replacement API: `Registrar`, `RegistryEntry`, `ItemEntry`, and `BlockEntry`. Although the public API changes are largely renames, the underlying implementation has been substantially redesigned.

### Deprecated
- Deprecated `Register`, `RegistryHolder`, `RegistryItem`, and `RegistryBlock`, marking them for removal. Use `Registrar`, `RegistryEntry`, `ItemEntry`, and `BlockEntry` instead.

### Fixed
- Removed the unused Mod Menu integration from the Fabric module.
- Corrected the mixin package reference in `xunlib.fabric.mixins.json`.