# Picocli Minecraft
[![](https://jitpack.io/v/Nowilltolife/picocli-minecraft.svg)](https://jitpack.io/#Nowilltolife/picocli-minecraft)
[![](https://img.shields.io/github/license/Nowilltolife/picocli-minecraft)](https://github.com/Nowilltolife/picocli-minecraft)

This project is small wrapper around [picocli](https://github.com/remkop/picocli) in the use of a command system.

- [Usage](#usage)
    - [Bukkit](#bukkit)
    - [BungeeCord](#bungeecord)
- [Examples](#examples)
- [Installation](#installation)
    - [Maven](#maven)
    - [Gradle](#gradle)
- [Credits](#credits)

## Usage
The command system is highly oriented around the already existing picocli syntax with a few extra added things.     
To register commands you must create a class that extends the respective platform base. After that picocli syntax can follow, with the
exception that there is an added annotation `@Permission` that can be added to `@Command` annotated classes or methods that will check for the
given permission before execution.       
For execution the `Base` class requires you to implement the `int execute()` method. For context related values the respetive 
Example for bukkit:
```java
@Command(
        name = "teleport",
        description = "Teleport players to a location",
        mixinStandardHelpOptions = true
)
@Permission(permission = "teleport.use")
public class TeleportCommand extends BukkitBase {

    @Option(names = {"-p"}, description="Player to teleport to", required=true)
    private Player p;
    
    public int execute() {
        if(getSender() instanceof Player) {
            ((Player)getSender()).teleport(p);
        }
    }

}
```
To then register the commands a `CommandManager` must be created and given a platform respective `ICommandRegistrar`.    
### Bukkit
```java
public final class Test extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager manager = new CommandManager(new BukkitCommandRegistrar(this));
        manager.register(new TeleportCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
```
### BungeeCord
```java
public final class Test extends Plugin {

    @Override
    public void onEnable() {
        CommandManager manager = new CommandManager(new BungeeCommandRegistrar(this));
        manager.register(new TeleportCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
```
`CommandManager` also allows you to define the help color scheme via `setColorScheme`
## Examples
`SpawnCommand`
```java
@Command(
        name = "spawn",
        description = "Spawn entities or blocks"
)
public class SpawnCommand extends BukkitBase {

    @Command(
            name = "entity",
            description = "Spawn an entity"
    )
    public int spawnEntity(@Parameters(paramLabel = "type", description = "The entity type to spawn") EntityType type) {
        if(getSender() instanceof Player) {
            Player player = (Player) getSender();
            player.getWorld().spawnEntity(player.getLocation(), type);
            return 0;
        } else {
            getSender().sendMessage("You must be a player to use this command");
            return 1;
        }
    }

    @Command(
            name = "block",
            description = "Spawn a block"
    )
    public int spawnBlock(@Parameters(paramLabel = "type", description = "The block type to spawn") Material material) {
        if(getSender() instanceof Player) {
            Player player = (Player) getSender();
            player.getWorld().getBlockAt(player.getLocation()).setType(material);
            return 0;
        } else {
            getSender().sendMessage("You must be a player to use this command");
            return 1;
        }
    }

    @Override
    public int execute() {
        // make it show the help message
        throw new ParameterException(getCommandLine(), "You must specify a subcommand");
    }
}
```
`TeleportCommand`
```java
@Command(
        name = "teleport",
        description = "Teleport players to a location",
        mixinStandardHelpOptions = true
)
public class TeleportCommand extends BukkitBase {

    @Parameters(description = "The x coordinate to teleport to", arity = "0..1", defaultValue = "_NULL_")
    Optional<Double> x;

    @Parameters(description = "The y coordinate to teleport to", arity = "0..1", defaultValue = "_NULL_")
    Optional<Double> y;

    @Parameters(description = "The z coordinate to teleport to", arity = "0..1", defaultValue = "_NULL_")
    Optional<Double> z;

    @Option(names = {"-p", "--player"}, description = "The player to teleport", arity = "0..1", defaultValue = "_NULL_")
    Optional<Player> player;

    @Option(names = {"-w", "--world"}, description = "The world to teleport to", arity = "0..1", defaultValue = "_NULL_")
    Optional<World> world;


    @Override
    public int execute() {
        Player executor = null;
        if(getSender() instanceof Player) {
            executor = (Player) getSender();
        }
        Player target = player.orElse(executor);
        if(target == null) {
            getSender().sendMessage(ChatColor.RED + "You must be a player to use this command");
            return 1;
        }
        World world = this.world.orElseGet(target::getWorld);
        if(x.isPresent() && y.isPresent() && z.isPresent()) {
            target.teleport(new Location(world, x.get(), y.get(), z.get()));
        } else {
            if(target != executor) {
                target.teleport(executor);
            } else {
                if(player.isPresent()) {
                    getSender().sendMessage(ChatColor.RED + "You cannot teleport to yourself!");
                } else {
                    throw new ParameterException(getCommandLine(), "You must specify a location to teleport to");
                }
            }
        }
        return 0;
    }
}
```

## Installation
To use this library in your own project you must add the following based on your platform:   
### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <dependency>
        <groupId>com.github.Nowilltolife</groupId>
        <artifactId>picocli-minecraft</artifactId>
        <version>1.0-beta</version>
    </dependency>
</dependencies>
```
### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
    implementation 'com.github.Nowilltolife:picocli-minecraft:1.0-beta'
}
```
## Credits
- [picocli](https://github.com/remkop/picocli)
- [objectweb](https://asm.ow2.io/)
