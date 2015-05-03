package de.slikey.batch.agent.execution;

import de.slikey.batch.agent.BatchAgent;
import de.slikey.batch.protocol.job.PacketJobConsoleOutput;
import io.netty.util.internal.PlatformDependent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Kevin
 * @since 28.04.2015
 */
public class JobExecutor implements Runnable {

    private static final Logger logger = LogManager.getLogger(JobExecutor.class.getSimpleName());

    private final BatchAgent batchAgent;
    private final UUID uuid;
    private final String command;
    private int exitCode;

    public JobExecutor(BatchAgent batchAgent, UUID uuid, String command) {
        this.batchAgent = batchAgent;
        this.uuid = uuid;

        if (PlatformDependent.isWindows())
            command = "cmd /c " + command;
        this.command = command;
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

            ConsoleReader console = new ConsoleReader(batchAgent, uuid, PacketJobConsoleOutput.Level.OUTPUT, process.getInputStream()).start(batchAgent.getThreadPool());
            ConsoleReader error = new ConsoleReader(batchAgent, uuid, PacketJobConsoleOutput.Level.ERROR, process.getErrorStream()).start(batchAgent.getThreadPool());

            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                exitCode = -1;
            }

            console.stop();
            error.stop();

            onStop();
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

}
