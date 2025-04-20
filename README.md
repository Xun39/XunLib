# Xun Lib 
*1.21.x | NeoForge/Forge/Fabric | MIT*

A **common code library mod** for Minecraft, providing reusable utilities for inventory/armor management, fuzzy item matching, effect handling, and other common utilities. Designed to simplify mod development by abstracting repetitive tasks.

**⚠️ Warning**: This mod is in **early development**. Untested code, bugs, and breaking changes are expected! Use with caution.

---

## Table of Contents
- [Installation](#installation)
- [Contributing](#contributing)
- [License](#license)
- [Disclaimer](#disclaimer) 

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
- **Distribution**: If you plan to distribute your mod, ensure the library JAR is included in your build outputs (see Gradle documentation for packaging).  

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
