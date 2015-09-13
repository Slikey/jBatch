package de.slikey.batch.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.slikey.batch.network.common.TPSManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Kevin
 * @since 03.05.2015
 */
public abstract class NIOComponent {

    private final TPSManager tpsManager;
    private final ScheduledExecutorService executorService;

    public NIOComponent(int threadCount) {
        this.tpsManager = new TPSManager();
        this.executorService = newExecutorService(threadCount);
    }

    public TPSManager getTpsManager() {
        return tpsManager;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    protected ScheduledExecutorService newExecutorService(int threadCount) {
        return Executors.newScheduledThreadPool(threadCount,
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat(this.getClass().getSimpleName() + "-%s")
                        .build()
        );
    }

}
