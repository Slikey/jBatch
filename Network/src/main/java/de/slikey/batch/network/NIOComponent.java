package de.slikey.batch.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kevin
 * @since 03.05.2015
 */
public class NIOComponent {

    protected ExecutorService newCachedThreadPool() {
        return Executors.newCachedThreadPool(
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat(this.getClass().getSimpleName() + "-%s")
                        .build()
        );
    }

}
