## XunLib 3.1.0

### Changed

- Moved `ItemStackPredicate` from `api.inventory` to `api.item`.
- Refactored `SlotRange`, renamed it to `ContainerSlotRange`, and moved it to `api.block.entity.container`. See the relevant commit for details.
- Updated `InventoryUtil` methods that previously accepted `SlotRange` to use `ContainerSlotRange` instead.
- Renamed `InventoryUtil.insertItem()` to `tryInsertStack()` for a clearer description of its behavior.

### Deprecated

- Deprecated all APIs under `api.inventory` and marked them for removal.
- Deprecated `PlayerInvUtil` because most of its methods were simple one-line wrappers around existing functionality.