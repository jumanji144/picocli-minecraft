# Command System
A simple command system that parses arguments directly.

## Usage
To define a command simply use the `@Command` annotation like this:
```java

public class AnyClass {
    
    @Command("command.subcommand.<number:int>.submit")
    public void onCommand(CommandContext ctx) {
        // Do something
    }
    
}
```
To define more complex commands with arguments you simply define a path   
For example: `command.subcommand` will be parsed as `command` and `subcommand`

### Argument parsing
The command system will automatically parse arguments for you given types.   
to use them simply add `<name:type>` to your command path and the context
will contain a argument with the parsed object

### Type parsers
For the types there needs to be a parser present, for primitives (int, double, etc.)
there is a default parser, but for custom types you need to register a parser via
`ArgumentParser.registerParser(String, Function<String, ?>)` where the first argument is the corresponding type name and
the second argument is a function that parses the string to the type.

## Setup

To use the system you simply construct a `CommandManager` with a `CommandLoader`.
Then to register commands simply use `CommandManager.registerCommands(Object)`. where Object is the class containing
atleast one `@Command` annotated method.
The plugins comes shipped with done implementations for both bukkit and bungeecord.

### Bukkit example
```java
public class MyPlugin extends JavaPlugin {
    
    private CommandManager commandManager;
    
    @Override
    public void onEnable() {
        this.commandManager = new CommandManager(new BukkitCommandLoader(this));
        this.commandManager.registerCommand(new AnyClass());
    }
    
    @Override
    public void onDisable() {
        this.commandManager.unregisterAll();
    }
    
}
```

### BungeeCord example
```java
public class MyPlugin extends Plugin {
    
    private CommandManager commandManager;
    
    @Override
    public void onEnable() {
        this.commandManager = new CommandManager(new BungeeCommandLoader(this));
        this.commandManager.registerCommand(new AnyClass());
    }
    
    @Override
    public void onDisable() {
        this.commandManager.unregisterAll();
    }
    
}
```

## API
While in the command methods you can use the `CommandContext` to get the sender and arguments.   
The most important method is `CommandContext#getArgument(String)` which will return the argument with the given name.
