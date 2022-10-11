package me.darknet.cli.command;

import java.util.concurrent.Callable;

/**
 * Command context class
 */
public abstract class Base implements Callable<Integer> {

    public abstract int execute();

    @Override
    public Integer call() throws Exception {
        return execute();
    }
}
