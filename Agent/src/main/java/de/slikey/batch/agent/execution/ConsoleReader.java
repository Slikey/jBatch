package de.slikey.batch.agent.execution;

import de.slikey.batch.agent.BatchAgent;
import de.slikey.batch.protocol.job.PacketJobConsoleOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * @author Kevin Carstens
 * @since 28.04.2015
 */
public class ConsoleReader implements Runnable {

    private final BatchAgent batchAgent;
    private final UUID uuid;
    private final PacketJobConsoleOutput.Level level;
    private final InputStream inputStream;
    private Thread currentThread;

    public ConsoleReader(BatchAgent batchAgent, UUID uuid, PacketJobConsoleOutput.Level level, InputStream inputStream) {
        this.batchAgent = batchAgent;
        this.uuid = uuid;
        this.level = level;
        this.inputStream = inputStream;
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

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while (!Thread.interrupted() && (line = in.readLine()) != null) {
                batchAgent.sendPacket(new PacketJobConsoleOutput(uuid, level, line));
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

        currentThread = null;
    }

}
