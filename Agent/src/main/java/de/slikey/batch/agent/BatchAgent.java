package de.slikey.batch.agent;

import de.slikey.batch.agent.monitoring.HealthManager;
import de.slikey.batch.network.client.NIOClient;
import de.slikey.batch.network.common.TPSManager;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.protocol.PacketKeepAlive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class BatchAgent extends NIOClient {

    private static final Logger logger = LogManager.getLogger(BatchAgent.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
        new BatchAgent("localhost", 8080).run();
    }

    private final ScheduledExecutorService threadPool;
    private final TPSManager tpsManager;
    private final HealthManager healthManager;
    private final KeepAliveManager keepAliveManager;

    public BatchAgent(String host, int port) {
        super(host, port);
        this.threadPool = newExecutorService(32);
        this.tpsManager = new TPSManager();
        this.healthManager = new HealthManager(tpsManager, this);
        this.keepAliveManager = new KeepAliveManager(tpsManager, this);
        this.setReconnect(true);
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    public TPSManager getTpsManager() {
        return tpsManager;
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }

    public KeepAliveManager getKeepAliveManager() {
        return keepAliveManager;
    }

    @Override
    protected PacketChannelInitializer buildPacketChannelInitializer() {
        return new AgentPacketChannelInitializer(this);
    }

    @Override
    protected void startClient() throws InterruptedException {
        logger.info("Starting managers...");
        healthManager.start(threadPool);
        keepAliveManager.start(threadPool);
        new TickingManager(tpsManager, 1) {

            @Override
            protected void onTick(double deltaSeconds) {
                for (int i = 0; i < 10; i++) {
                    sendPacket(new PacketKeepAlive());
                }
            }

        }.start(threadPool);
        logger.info("Successfully started managers.");

        waitForShutdown();
    }

    private void waitForShutdown() throws InterruptedException {
        synchronized (this) {
            wait();
        }
        logger.info("Shutdown requested...");

        threadPool.shutdownNow();
        threadPool.awaitTermination(5, TimeUnit.SECONDS);

        logger.info("Successfully shutdown.");
    }

}
