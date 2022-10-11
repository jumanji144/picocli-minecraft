# Picocli Minecraft
This project is small wrapper around [picocli](https://github.com/remkop/picocli) in the use of a command system.

## Usage
The command system is highly oriented around the already existing picocli syntax with a few extra added things.     
To register commands you must create a class that extends the respective platform base. After that picocli syntax can follow, with the
exception that there is a added annotation `@Permission` that can be added to `@Command` annotated classes or methods that will check for the
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
Bukkit example:
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
`CommandManager` also allows you to define the help color scheme via `setColorScheme`

## Credits
- [picocli](https://github.com/remkop/picocli)
- [objectweb](https://asm.ow2.io/)
