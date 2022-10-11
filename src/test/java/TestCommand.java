import org.junit.Test;
import picocli.CommandLine;

import static picocli.CommandLine.*;

@Command(name = "test", mixinStandardHelpOptions = true)
public class TestCommand implements Runnable{

    @Option(names = { "-n" }, description = "Number", required = true)
    private int num;

    @Override
    public void run() {
        System.out.println("Number: " + num);
    }

    @Test
    public void test() {
        CommandLine.run(new TestCommand(), "-n", "123");
    }
}
