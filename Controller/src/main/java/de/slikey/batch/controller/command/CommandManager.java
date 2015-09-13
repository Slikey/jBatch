package de.slikey.batch.controller.command;

import com.google.common.reflect.ClassPath;
import de.slikey.batch.controller.MainController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Kevin
 * @since 09.09.2015
 */
public class CommandManager implements Runnable {

    private static final Logger logger = LogManager.getLogger(CommandManager.class.getSimpleName());

    private final MainController mainController;
    private final List<Command> commandList;
    private Thread thread;

    public CommandManager(MainController mainController) {
        this.mainController = mainController;
        this.commandList = new CopyOnWriteArrayList<>();

        initialize();
    }

    private void initialize() {
        try {
            final ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());

            String report = "Registering Commands: \n{\n";
            for (ClassPath.ResourceInfo resourceInfo : classPath.getResources()) {
                String className = resourceInfo.toString();
                if (className.contains(".Command")) {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.getSuperclass() == Command.class) {
                        Constructor<?> constructor = clazz.getConstructor(MainController.class);
                        Command command = (Command) constructor.newInstance(mainController);
                        report += "\t{label='" + command.getLabel() + "'";
                        if (command.getAliases() != null) {
                            report += ", aliases=" + Arrays.toString(command.getAliases()) + "";
                        }
                        report += "},\n";
                        commandList.add(command);
                    }
                }
            }

            report = report.substring(0, report.length() - 2) + "\n}";
            logger.info(report);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        stop();

        thread = new Thread(this);
        thread.setName("ConsoleInput-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (!Thread.interrupted()) {
                String line = scanner.nextLine();
                if (line != null) {
                    String[] args = line.split(" ");
                    if (args.length > 0) {
                        boolean executed = false;
                        for (Command command : commandList) {
                            if (command.shouldHandle(args)) {
                                logger.info("Command '" + command.getLabel() + "' issued...");
                                if (command.execute(args)) {
                                    executed = true;
                                    logger.info("Command '" + command.getLabel() + "' executed!");
                                } else {
                                    logger.info("Wrong usage! Usage: " + command.getUsage());
                                }
                                break;
                            }
                        }
                        if (!executed) {
                            logger.info("Command '" + args[0] + "' not found!");
                        }
                    }
                }
            }
        } catch (NoSuchElementException ignored) {

        }
    }

}
