import me.darknet.commandsystem.command.Argument;
import me.darknet.commandsystem.command.Arguments;
import me.darknet.commandsystem.parser.ArgumentNode;
import me.darknet.commandsystem.parser.ArgumentParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParseTest {

    @Test
    public void testPathParsing() {
        String path = "test.<test:Test>.test.<test2:Test2>.<test3:Test3>.test";
        Argument[] arguments = ArgumentParser.parseArguments(path);

        // assertions
        Assertions.assertEquals(6, arguments.length);
        Assertions.assertEquals(0, arguments[0].getPlace());
        Assertions.assertEquals("test", arguments[0].getName());
        Assertions.assertNull(arguments[0].getType());
        Assertions.assertEquals(1, arguments[1].getPlace());
        Assertions.assertEquals("test", arguments[1].getName());
        Assertions.assertEquals("Test", arguments[1].getType());
        Assertions.assertEquals(2, arguments[2].getPlace());
        Assertions.assertEquals("test", arguments[2].getName());
        Assertions.assertNull(arguments[2].getType());
        Assertions.assertEquals(3, arguments[3].getPlace());
        Assertions.assertEquals("test2", arguments[3].getName());
        Assertions.assertEquals("Test2", arguments[3].getType());
        Assertions.assertEquals(4, arguments[4].getPlace());
        Assertions.assertEquals("test3", arguments[4].getName());
        Assertions.assertEquals("Test3", arguments[4].getType());
        Assertions.assertEquals(5, arguments[5].getPlace());
        Assertions.assertEquals("test", arguments[5].getName());
        Assertions.assertNull(arguments[5].getType());
    }

    @Test
    public void testPathLookup() {

        Argument[] compiledPath = ArgumentParser.parseArguments("test1.test2.test3");
        Argument[] compiledPath2 = ArgumentParser.parseArguments("test1.test2.test4");
        Argument[] compiledPath3 = ArgumentParser.parseArguments("test1.<number:int>.test4");
        Argument[] compiledPath4 = ArgumentParser.parseArguments("test1.<number:int>.test4.<number1:int>");

        ArgumentNode node = ArgumentNode.toNode(compiledPath);
        ArgumentNode node2 = ArgumentNode.toNode(compiledPath2);
        ArgumentNode node3 = ArgumentNode.toNode(compiledPath3);
        ArgumentNode node4 = ArgumentNode.toNode(compiledPath4);

        Arguments arguments = new Arguments();

        arguments.getBuiltTree().getRoot().addChild(node);
        arguments.getBuiltTree().getRoot().addChild(node2);
        arguments.getBuiltTree().getRoot().addChild(node3);
        arguments.getBuiltTree().getRoot().addChild(node4);

        Argument[] lookup = arguments.lookup(new String[] {"test1", "test2"});
        Assertions.assertEquals(2, lookup.length);
        Assertions.assertEquals("test1", lookup[0].getName());
        Assertions.assertEquals("test2", lookup[1].getName());

        lookup = arguments.lookup(new String[] {"test1", "test2", "test3"});
        Assertions.assertEquals(3, lookup.length);
        Assertions.assertEquals("test1", lookup[0].getName());
        Assertions.assertEquals("test2", lookup[1].getName());

        lookup = arguments.lookup(new String[] {"test1", "test2", "test4"});
        Assertions.assertEquals(3, lookup.length);
        Assertions.assertEquals("test1", lookup[0].getName());
        Assertions.assertEquals("test2", lookup[1].getName());

        lookup = arguments.lookup(new String[] {"test1", "test2", "test5"});
        Assertions.assertEquals(2, lookup.length);
        Assertions.assertEquals("test1", lookup[0].getName());
        Assertions.assertEquals("test2", lookup[1].getName());

        lookup = arguments.lookup(new String[] {"test1", "3"});
        Assertions.assertEquals(2, lookup.length);
        Assertions.assertEquals("test1", lookup[0].getName());
        Assertions.assertEquals("number", lookup[1].getName());

        lookup = arguments.lookup(new String[] {"test1", "3", "test4"});
        Assertions.assertEquals(3, lookup.length);
        Assertions.assertEquals("test1", lookup[0].getName());
        Assertions.assertEquals("number", lookup[1].getName());

        lookup = arguments.lookup(new String[] {"test1", "3", "test4", "4"});
        Assertions.assertEquals(4, lookup.length);
        Assertions.assertEquals("test1", lookup[0].getName());
        Assertions.assertEquals("number", lookup[1].getName());
        Assertions.assertEquals("number1", lookup[3].getName());


        return;

    }

}
