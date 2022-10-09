import me.darknet.commandsystem.CommandManager;
import me.darknet.commandsystem.command.AbstractCommandLoader;
import me.darknet.commandsystem.command.Arguments;
import me.darknet.commandsystem.command.Command;
import me.darknet.commandsystem.command.CommandContext;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class CommandLoadingTest {

    @Test
    public void testLoad() {
        CommandManager manager = new CommandManager(new TestCommandLoader());
        manager.registerCommand(new TestCommand());
    }

    public class TestCommandLoader extends AbstractCommandLoader {
        @Override
        public boolean registerCommand(Command annotation, Arguments arguments, Object command) {
            Assertions.assertEquals("test", annotation.value());
            return true;
        }

        @Override
        public boolean unregisterCommand0(Object command) {
            return false;
        }

        @Override
        public void registerParsers() {
            // no new parsers to register
        }
    }

    public class TestCommand {

        @Command("test")
        public void onTestCommand(CommandContext context) {
            System.out.println("test");
        }
    }

}
