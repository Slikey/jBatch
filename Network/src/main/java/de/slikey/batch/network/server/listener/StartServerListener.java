package de.slikey.batch.network.server.listener;

import de.slikey.batch.network.server.NIOServer;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class StartServerListener implements GenericFutureListener<Future<Void>> {

    private static final Logger logger = LogManager.getLogger(NIOServer.class);

    private final NIOServer server;

    public StartServerListener(NIOServer server) {
        this.server = server;
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        logger.info("Successfully bound to port " + server.getPort() + "!");
        server.startApplication();
    }

}
