# Xun Lib 
*1.21.x | NeoForge/Forge/Fabric | MIT*

A **common code library mod** for Minecraft, providing reusable utilities for inventory/armor management, fuzzy item matching, effect handling, and other common utilities. Designed to simplify mod development by abstracting repetitive tasks.

**⚠️ Warning**: This mod is in **early development**. Untested code, bugs, and breaking changes are expected! Use with caution.

---

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [Contributing](#contributing)
- [License](#license)
- [Disclaimer](#disclaimer)
  
---

## Features

### **Common Utilities**
- **General Helpers**: Simplify repetitive tasks with utility methods for logging, validation, and world interactions.
- **Block Position Tools**: 
  - Calculate distances, and iterate over block areas.
- **Color Utilities**:
  - **Conversions**: Seamlessly convert between RGB, HSV, and HSL color spaces.
  - **Blending**: Blend colors with customizable interpolation modes.
  - **Classes**: `HSVColor` and `HSLColor` classes for advanced color manipulation.

---

### **Inventory & Armor Management**
- **Container Agnostic**:
  - Insert/extract items from any container type (chests, hoppers, etc.) with built-in slot validation.
  - Check item compatibility and simulate transfers without modifying inventories.
- **Armor-Specific Tools**:
  - Equip/unequip armor items with durability checks.
  - Validate armor slots based on item type or custom rules.

---

### **Fuzzy Item Matching**
- **Customizable Comparison**:
  - Compare items by count, enchantments, NBT tags, or durability.
  - Ignore specific properties (e.g., ignore count for "any amount" checks).
- **Advanced Filters**:
  - Whitelist/blacklist items by data components, ...*(not implemented yet)*
  - Chain multiple conditions for complex matching logic.

---

### **Mob Effect Utilities**
- **Effect Management**:
  - Apply/remove effects with duration and amplifier control.

---

### **@PersistentNbt Annotation**
- **Auto-Serialization**:
  - Annotate fields in `BlockEntity` classes to automatically save/load them to NBT.
  - Supports primitives, strings, lists, and compound tags.
- **Reduce Boilerplate**:
  - Eliminate manual `saveAdditional`/`loadAdditional` overrides for annotated fields.
---

## Installation
Add the jar file to your project and write this in your **`build.gradle`** file:
   ```gradle  
   dependencies {  
       // Other dependencies...  
       implementation files("libs/xunlib-[loader]-[version].jar")  // basically the path, if you put that inside of api/ instead libs/, change it.
   }  
   ```  
   - Replace `[loader]` with the loader that you use.
   - Replace `[version]` with the version that you want.
---

### **Notes**  
- **Path Troubleshooting**: If Gradle fails to find the JAR, double-check:  
  - The JAR is in the correct folder (e.g., `libs`).  
  - The filename in `build.gradle` matches the actual JAR filename **exactly** (case-sensitive). 

---

## Contributing  
This project is open to contributions!  
1. **Report Bugs**: Open an issue on [GitHub Issues](https://github.com/Xun39/XunLib/issues).
2. **Pull Requests**: Fork the repo and submit a PR (include tests if possible).  

---

## License  
This mod is licensed under **[MIT License](LICENSE)**.  

---

## Disclaimer  
**This mod is experimental!**  
- Untested code may cause crashes or unexpected behavior.  
- Always back up your world before testing.  
- API methods may change in future updates.  
