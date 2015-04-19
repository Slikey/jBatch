package de.slikey.batch.network.client.listener;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class StartClientListener implements GenericFutureListener<Future<Void>> {

    private final String host;
    private final int port;

    public StartClientListener(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        System.out.println("Successfully connected to " + host + ":" + port + "!");
    }

}

