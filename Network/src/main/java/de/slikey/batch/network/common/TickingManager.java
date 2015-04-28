package de.slikey.batch.network.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;

/**
 * @author Kevin Carstens
 * @since 26.04.2015
 */
public abstract class TickingManager implements Runnable {

    private static Logger logger = LogManager.getLogger(TickingManager.class.getSimpleName());

    private Thread currentThread;
    private long interval;
    private long lastExecution;

    public TickingManager(long interval) {
        this.interval = interval;
    }

    public void start(ExecutorService executorService) {
        stop();

        String name = this.getClass().getSimpleName();
        LogManager.getLogger(name).info("Started " + name + "!");
        executorService.execute(this);
    }

    public void stop() {
        if (currentThread != null) {
            currentThread.interrupt();
        }
    }

    @Override
    public final void run() {
        String name = this.getClass().getSimpleName();
        Logger childLogger =  LogManager.getLogger(name);

        currentThread = Thread.currentThread();
        try {
            while (!Thread.interrupted()) {
                long currentTime = System.currentTimeMillis();
                long delta = currentTime - lastExecution;
                double deltaSeconds = delta / 1000d;

                try {
                    onTick(deltaSeconds);
                } catch (Exception | ExceptionInInitializerError e) {
                    childLogger.error("Exception occurred during tick.", e);
                }

                lastExecution = System.currentTimeMillis();
                long timeTaken = lastExecution - currentTime;

                long sleep = interval - timeTaken;
                if (sleep > 0) {
                    Thread.sleep(sleep);
                } else {
                    childLogger.warn("Tick took " + timeTaken + "ms! Goal was " + interval + "! (" + this.getClass().getName() + ")");
                }
            }
        } catch (InterruptedException e) {
            // Interrupted running Thread
        }
        currentThread = null;

        childLogger.info("Stopped " + name + "!");
    }

    protected abstract void onTick(double deltaSeconds);

}
