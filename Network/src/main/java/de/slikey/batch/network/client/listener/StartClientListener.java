package de.slikey.batch.network.client.listener;

import de.slikey.batch.network.client.NIOClient;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class StartClientListener implements GenericFutureListener<Future<Void>> {

    private static final Logger logger = LogManager.getLogger(NIOClient.class.getSimpleName());

    private final String host;
    private final int port;

    public StartClientListener(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        logger.info("Successfully connected to " + host + ":" + port + "!");
    }

}

