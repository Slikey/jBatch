package de.slikey.batch.agent.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Kevin
 * @since 28.04.2015
 */
public class JobExecutor implements Runnable {

    private static final Logger logger = LogManager.getLogger(JobExecutor.class.getSimpleName());

    private final String command;
    private int exitCode;

    public JobExecutor(String command) {
        //if (PlatformDependent.isWindows())
        //    command = command;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
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

            String line;
            Process process = Runtime.getRuntime().exec(command);
            Reader r = new InputStreamReader(process.getErrorStream());
            BufferedReader in = new BufferedReader(r);
            while ((line = in.readLine()) != null) {
                System.err.println(line);
            }
            in.close();
            exitCode = process.exitValue();

            onStop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
