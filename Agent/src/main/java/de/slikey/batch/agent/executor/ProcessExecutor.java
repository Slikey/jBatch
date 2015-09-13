package de.slikey.batch.agent.executor;

import de.slikey.batch.agent.MainClient;
import de.slikey.batch.protocol.PacketConsoleOutput;
import io.netty.util.internal.PlatformDependent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Kevin
 * @since 08.09.2015
 */
public class ProcessExecutor implements Runnable {

    private static final Logger logger = LogManager.getLogger(ProcessExecutor.class.getSimpleName());

    private final MainClient mainClient;
    private final UUID uuid;
    private final String command;
    private int exitCode;

    public ProcessExecutor(MainClient mainClient, UUID uuid, String command) {
        this.mainClient = mainClient;
        this.uuid = uuid;

        if (PlatformDependent.isWindows()) {
            command = "cmd /c " + command;
        }
        this.command = command;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }

    public int getExitCode() {
        return exitCode;
    }

    protected void onStart() {
        logger.info("Execution of '" + command + "' started...");
    }

    protected void onStop() {
        logger.info("Execution of '" + command + "' finished with code: " + exitCode);
    }

    @Override
    public void run() {
        try {
            onStart();

            Process process = Runtime.getRuntime().exec(command);

            ConsoleReader console = new ConsoleReader(mainClient, uuid, PacketConsoleOutput.Level.OUTPUT, process.getInputStream()).start(mainClient.getExecutorService());
            ConsoleReader error = new ConsoleReader(mainClient, uuid, PacketConsoleOutput.Level.ERROR, process.getErrorStream()).start(mainClient.getExecutorService());

            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                process.destroy();
                exitCode = -1;
            }

            console.stop();
            error.stop();

            onStop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
