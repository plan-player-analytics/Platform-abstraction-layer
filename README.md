# Platform abstraction layer

Platform abstraction layer is a library for abstracting away some server platform specific implementations.

Compared to the [the previous abstraction library](https://github.com/AuroraLS3/Abstract-Plugin-Framework), this library
is more modular and main goal was to use composition rather than inheritance to gain access to the platform
implementations. This allows users of this API to use other libraries without worrying about compatibility issues.

### Supported Minecraft server-platforms

- [Spigot](https://www.spigotmc.org/)
- [Folia](https://papermc.io/software/folia)
- [Sponge](https://www.spongepowered.org/)
- [Nukkit](https://cloudburstmc.org/)
- [BungeeCord](https://www.spigotmc.org/wiki/bungeecord/)
- [Velocity](https://www.velocitypowered.com/)

### Building

```bash
./gradlew build
```

### Usage

```groovy
repositories {
    maven { // Platform abstraction layer repository
        url = "https://repo.playeranalytics.net/releases"
    }
}

ext.palVersion = "5.3.0"

dependencies {
    implementation "net.playeranalytics:platform-abstraction-layer-api:$palVersion"
    
    // Pick your platform(s)
    implementation "net.playeranalytics:platform-abstraction-layer-bukkit:$palVersion"
    implementation "net.playeranalytics:platform-abstraction-layer-folia:$palVersion"
    implementation "net.playeranalytics:platform-abstraction-layer-bungeecord:$palVersion"
    implementation "net.playeranalytics:platform-abstraction-layer-nukkit:$palVersion"
    implementation "net.playeranalytics:platform-abstraction-layer-sponge:$palVersion"
    implementation "net.playeranalytics:platform-abstraction-layer-velocity:$palVersion"
}
```

Include this library in your project and shade/shadow the library classes into the final artifact.  
Relocate `net.playeranalytics.plugin` to a different location to avoid conflicts.

Access the API:

```java
PlatformAbstractionLayer layer;

// org.bukkit.plugin.java.JavaPlugin
layer =new

BukkitPlatformLayer(javaPlugin);
// org.bukkit.plugin.java.JavaPlugin
layer =new

FoliaPlatformLayer(javaPlugin); // Throws IllegalStateException on non-Folia servers
// Object (has @Plugin annotation), File, org.slf4j.Logger
layer = new SpongePlatformLayer(plugin, dataFolder, logger); 
// cn.nukkit.plugin.PluginBase
layer = new NukkitPlatformLayer(pluginBase);
// net.md_5.bungee.api.plugin.Plugin
layer = new BungeePlatformLayer(plugin);
// Object (has @Plugin annotation), ProxyServer, org.slf4j.Logger, Path
layer = new VelocityPlatformLayer(plugin, proxy, logger, dataFolderPath);     
```

See the javadoc for further details on each feature `PlatformAbstractionLayer` provides.

### Features

- Console logging
- Access to plugin meta-data, jar-resources and configuration folder
- Access to platform task scheduling
- Managing listeners of specific platform
