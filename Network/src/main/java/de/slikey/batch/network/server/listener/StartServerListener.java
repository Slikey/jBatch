package de.slikey.batch.network.server.listener;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class StartServerListener implements GenericFutureListener<Future<Void>> {

    private final int port;

    public StartServerListener(int port) {
        this.port = port;
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        System.out.println("Successfully bound to port " + port + "!");
    }

}
