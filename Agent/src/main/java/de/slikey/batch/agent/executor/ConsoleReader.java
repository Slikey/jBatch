package de.slikey.batch.agent.executor;

import de.slikey.batch.agent.MainClient;
import de.slikey.batch.protocol.PacketConsoleOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * @author Kevin
 * @since 08.09.2015
 */
public class ConsoleReader implements Runnable {

    private static final Logger logger = LogManager.getLogger(ConsoleReader.class.getSimpleName());

    private final MainClient mainClient;
    private final UUID uuid;
    private final PacketConsoleOutput.Level level;
    private final InputStream inputStream;
    private Thread currentThread;
    private boolean output;

    public ConsoleReader(MainClient mainClient, UUID uuid, PacketConsoleOutput.Level level, InputStream inputStream) {
        this.mainClient = mainClient;
        this.uuid = uuid;
        this.level = level;
        this.inputStream = inputStream;
        this.output = true;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public ConsoleReader start(ExecutorService executorService) {
        stop();
        executorService.execute(this);
        return this;
    }

    public void stop() {
        if (currentThread != null)
            currentThread.interrupt();
    }

    @Override
    public void run() {
        currentThread = Thread.currentThread();

        logger.info("Started " + level + "-Reader for " + uuid + "!");

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while (!Thread.interrupted() && (line = in.readLine()) != null) {
                mainClient.sendPacket(new PacketConsoleOutput(uuid, level, line));
                if (output) {
                    logger.info("[" + level + "] " + uuid + ": " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("Stopped " + level + "-Reader for " + uuid + "!");

        currentThread = null;
    }

}
