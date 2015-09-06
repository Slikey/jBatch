package de.slikey.batch.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Kevin
 * @since 03.05.2015
 */
public abstract class NIOComponent {

    protected ScheduledExecutorService newExecutorService(int threadCount) {
        return Executors.newScheduledThreadPool(threadCount,
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat(this.getClass().getSimpleName() + "-%s")
                        .build()
        );
    }

}
