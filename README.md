# XunLib
*Minecraft 1.21.1 · Fabric / NeoForge · MIT*

[![CurseForge](https://img.shields.io/badge/CurseForge-XunLib-orange?logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/xunlib)
[![Modrinth](https://img.shields.io/badge/Modrinth-XunLib-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/mod/xunlib)

A **common code library for Minecraft mod development**, providing reusable APIs and utilities to reduce boilerplate and make mod development easier.

> **⚠️ Note:** XunLib is actively developed. APIs may change between releases, particularly across major versions.

---

## Table of Contents

- [Features](#features)
    - [Utilities](#utilities)
    - [Registration](#registration)
    - [Configuration](#configuration)
    - [Item Sets](#item-sets)
    - [Effects](#effects)
    - [Block Entities](#block-entities)
    - [World & Structures](#world--structures)
    - [Client & GUI](#client--gui)
- [Installation](#installation)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Utilities

A collection of reusable utilities for common mod-development tasks.

Includes utilities for:

- Inventories and equipment
- Item stacks
- Block positions and areas
- Colors
- Resources and translations
- Registries
- World interactions

---

### Registration

A simple, strongly typed registration API for common mod development.

Provides reusable registration abstractions for items, blocks, and other registry objects while keeping registration code concise.

---

### Configuration

A complete configuration system with support for:

- Common, client, and server configurations
- Nested configuration groups
- Validation and numeric constraints
- Conditional options
- Translatable names and descriptions
- Automatic JSON serialization
- In-game configuration screens

See the [documentation](#documentation) for details.

---

### Item Sets

Reusable APIs for creating related groups of items.

Includes support for:

- Tool sets
- Armor sets
- Custom item creation
- Customization of item properties and attributes

---

### Effects

Utilities for working with mob effects and effect instances.

Provides convenient effect creation, application, and configurable effect stacking behavior.

---

### Block Entities

Common APIs for working with block entities and their supporting systems.

Includes ticking interfaces, container-related utilities, and helpers for creating block entity types from common code.

---

### World & Structures

Utilities and APIs for world-related development.

Includes spatial utilities, rectangular areas, structure helpers, and terrain-aware Jigsaw structure support.

---

### Client & GUI

Client-side utilities and reusable GUI components.

XunLib also provides the client interface used by its configuration system, making it possible to build configurable mods without creating an entire configuration UI from scratch.

---

## Installation

Download the appropriate XunLib release for your Minecraft version and loader from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/xunlib) or [Modrinth](https://modrinth.com/mod/xunlib).

For development, add the XunLib JAR to your project's libraries and declare it as a dependency in Gradle.

Example:

```gradle
dependencies {
    implementation files("libs/xunlib-neoforge-1.21.1-3.0.0.jar")
}
```

For Fabric, use the corresponding Fabric artifact.

The exact artifact name depends on the loader and XunLib version.

---

## Documentation

Detailed API documentation and guides are available on the [XunLib Wiki](https://github.com/Xun39/XunLib/wiki).

You can also refer to the [source code](https://github.com/Xun39/XunLib) and the [CHANGELOG](CHANGELOG.md) for implementation details and release-specific changes.

---

## Contributing

Contributions are welcome.

For bugs, suggestions, and feature requests, please use [GitHub Issues](https://github.com/Xun39/XunLib/issues).

Pull requests are also welcome. For larger API changes, please consider opening an issue first so the design can be discussed before implementation.

---

## License

XunLib is licensed under the **[MIT License](LICENSE)**.