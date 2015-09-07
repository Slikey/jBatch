package de.slikey.batch.network.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Carstens
 * @since 26.04.2015
 */
public abstract class TickingManager implements Runnable {

    private static Logger logger = LogManager.getLogger(TickingManager.class.getSimpleName());

    private final TPSManager tpsManager;
    private final Logger childLogger;
    private ScheduledExecutorService executorService;
    private long interval;
    private long lastExecution;
    private boolean running;

    public TickingManager(TPSManager tpsManager, long interval) {
        this.tpsManager = tpsManager;
        this.interval = interval;
        this.running = false;

        String name = this.getClass().getSimpleName();
        this.childLogger = LogManager.getLogger(name);
    }

    public void start(ScheduledExecutorService executorService) {
        if (running) {
            stop();
        }
        running = true;

        try {
            onStart();
        } catch (Exception e) {
            childLogger.error("Error occurred in onStart() method. Manager not starting.", e);
            return;
        }

        this.executorService = executorService;
        String name = this.getClass().getSimpleName();
        LogManager.getLogger(name).info("Started " + name + "!");
        executorService.execute(this);
    }

    public void stop() {
        executorService = null;

        if (running) {
            running = false;
            try {
                onStop();
            } catch (Exception e) {
                childLogger.error("Error occurred in onStop() method. Forcing stop.", e);
            }
        }
    }

    protected void onStart() {

    }

    protected void onStop() {

    }

    @Override
    public final void run() {
        if (running) {
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - lastExecution;
            double deltaSeconds = delta / 1000d;

            try {
                onTick(deltaSeconds);
            } catch (Exception | ExceptionInInitializerError e) {
                childLogger.error("Exception occurred during tick.", e);
            }

            lastExecution = System.currentTimeMillis();
            tpsManager.reportTime(this, lastExecution);
            long timeTaken = lastExecution - currentTime;

            long sleep = interval - timeTaken;
            if (running) {
                if (sleep > 0) {
                    executorService.schedule(this, sleep, TimeUnit.MILLISECONDS);
                } else {
                    executorService.submit(this);
                    if (interval > 0) {
                        childLogger.warn("Tick took " + timeTaken + "ms! Goal was " + interval + "! (" + this.getClass().getName() + ")");
                    }
                }
            }
        }
    }

    protected abstract void onTick(double deltaSeconds);

}
