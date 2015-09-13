package de.slikey.batch.network.common;

import de.slikey.batch.network.NIOComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Carstens
 * @since 26.04.2015
 */
public abstract class TickingManager implements Runnable {

    private static Logger logger = LogManager.getLogger(TickingManager.class.getSimpleName());

    private final NIOComponent nioComponent;
    private final TPSManager tpsManager;
    private final Logger childLogger;
    private final ScheduledExecutorService executorService;
    private Future<?> future;
    private long interval;
    private long lastExecution;
    private boolean running;

    public TickingManager(NIOComponent nioComponent, long interval) {
        this.nioComponent = nioComponent;
        this.tpsManager = nioComponent.getTpsManager();
        this.executorService = nioComponent.getExecutorService();
        this.interval = interval;
        this.running = false;

        String name = this.getClass().getSimpleName();
        this.childLogger = LogManager.getLogger(name);
    }

    public void start() {
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

        String name = this.getClass().getSimpleName();
        LogManager.getLogger(name).info("Started " + name + "!");
        future = executorService.submit(this);
    }

    public void stop() {
        if (running) {
            future.cancel(false);
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
                    future = executorService.schedule(this, sleep, TimeUnit.MILLISECONDS);
                } else {
                    future = executorService.submit(this);
                    if (interval > 0) {
                        childLogger.warn("Tick took " + timeTaken + "ms! Goal was " + interval + "! (" + this.getClass().getName() + ")");
                    }
                }
            }
        }
    }

    protected abstract void onTick(double deltaSeconds);

}
