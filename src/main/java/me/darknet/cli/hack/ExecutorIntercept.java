package me.darknet.cli.hack;

import me.darknet.cli.command.Permission;
import me.darknet.cli.util.Reflection;
import picocli.CommandLine;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

import static picocli.CommandLine.printHelpIfRequested;

/**
 * Hack class to intercept the .execute call in picocli to parse user data and include it in the
 * command execution.
 */
public class ExecutorIntercept extends CommandLine.RunLast {

    private final BiFunction<Object, String, Boolean> permissionCheck;

    private Object currentCaller;

    public ExecutorIntercept(BiFunction<Object, String, Boolean> permissionCheck) {
        this.permissionCheck = permissionCheck;
    }

    public void prepareExecution(Object caller) {
        currentCaller = caller;
    }

    public void finishExecution() {
        currentCaller = null;
    }

    public List<Object> handleParseResult(List<CommandLine> parsedCommands, PrintStream out, CommandLine.Help.Ansi ansi) {
        if (printHelpIfRequested(parsedCommands, out, err(), ansi)) { return returnResultOrExit(Collections.emptyList()); }
        return returnResultOrExit(executeUserObjectOfLastSubcommandWithSameParent(parsedCommands));
    }

    protected List<Object> handle(CommandLine.ParseResult parseResult) throws CommandLine.ExecutionException {
        return executeUserObjectOfLastSubcommandWithSameParent(parseResult.asCommandLineList());
    }
    private List<Object> executeUserObjectOfLastSubcommandWithSameParent(List<CommandLine> parsedCommands) {
        int start = indexOfLastSubcommandWithSameParent(parsedCommands);
        List<Object> result = new ArrayList<>();
        for (int i = start; i < parsedCommands.size(); i++) {
            executeUserObject(parsedCommands.get(i), result);
        }
        return result;
    }

    private static int indexOfLastSubcommandWithSameParent(List<CommandLine> parsedCommands) {
        int start = parsedCommands.size() - 1;
        for (int i = parsedCommands.size() - 2; i >= 0; i--) {
            if (parsedCommands.get(i).getParent() != parsedCommands.get(i + 1).getParent()) { break; }
            start = i;
        }
        return start;
    }

    private List<Object> executeUserObject(CommandLine parsed, List<Object> executionResultList) {
        Object command = parsed.getCommand();
        Permission permission = null;
        if(command instanceof Runnable || command instanceof Callable) {
            Class<?> clazz = command.getClass();
            if(clazz.isAnnotationPresent(Permission.class)) {
                permission = clazz.getAnnotation(Permission.class);
            }
        } else if(command instanceof Method) {
            Method method = (Method) command;
            if(method.isAnnotationPresent(Permission.class)) {
                permission = method.getAnnotation(Permission.class);
            }
        }
        if(permission != null) {
            if(permissionCheck != null) {
                if(!permissionCheck.apply(currentCaller, permission.permission())) {
                    throw new CommandLine.ExecutionException(parsed, permission.noPermissionMessage());
                }
            }
        }
        if (command instanceof Runnable) {
            try {
                ((Runnable) command).run();
                parsed.setExecutionResult(null); // 4.0
                executionResultList.add(null); // for compatibility with picocli 2.x
                return executionResultList;
            } catch (CommandLine.ParameterException | CommandLine.ExecutionException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new CommandLine.ExecutionException(parsed, "Error while running command (" + command + "): " + ex, ex);
            }
        } else if (command instanceof Callable) {
            try {
                @SuppressWarnings("unchecked") Callable<Object> callable = (Callable<Object>) command;
                Object executionResult = callable.call();
                parsed.setExecutionResult(executionResult);
                executionResultList.add(executionResult);
                return executionResultList;
            } catch (CommandLine.ParameterException | CommandLine.ExecutionException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new CommandLine.ExecutionException(parsed, "Error while calling command (" + command + "): " + ex, ex);
            }
        } else if (command instanceof Method) {
            try {
                Method method = (Method) command;
                Object[] parsedArgs = Reflection.invoke(parsed.getCommandSpec(),
                        "commandMethodParamValues()[java/lang/Object;");
                Object executionResult;
                if (Modifier.isStatic(method.getModifiers())) {
                    executionResult = method.invoke(null, parsedArgs); // invoke static method
                } else if (parsed.getCommandSpec().parent() != null) {
                    executionResult = method.invoke(parsed.getCommandSpec().parent().userObject(), parsedArgs);
                } else {
                    executionResult = method.invoke(parsed.getFactory().create(method.getDeclaringClass()), parsedArgs);
                }
                parsed.setExecutionResult(executionResult);
                executionResultList.add(executionResult);
                return executionResultList;
            } catch (InvocationTargetException ex) {
                Throwable t = ex.getTargetException();
                if (t instanceof CommandLine.ParameterException) {
                    throw (CommandLine.ParameterException) t;
                } else if (t instanceof CommandLine.ExecutionException) {
                    throw (CommandLine.ExecutionException) t;
                } else {
                    throw new CommandLine.ExecutionException(parsed, "Error while calling command (" + command + "): " + t, t);
                }
            } catch (Exception ex) {
                throw new CommandLine.ExecutionException(parsed, "Unhandled error while calling command (" + command + "): " + ex, ex);
            }
        }
        if (parsed.getSubcommands().isEmpty()) {
            throw new CommandLine.ExecutionException(parsed, "Parsed command (" + command + ") is not a Method, Runnable or Callable");
        } else {
            throw new CommandLine.ParameterException(parsed, "Missing required subcommand");
        }
    }

}
