package de.slikey.batch.agent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.slikey.batch.network.client.NIOClient;
import de.slikey.batch.network.common.TPSManager;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class BatchAgent extends NIOClient {

    private static final Logger logger = LogManager.getLogger(BatchAgent.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
        new BatchAgent("localhost", 8080).run();
    }

    private final ExecutorService threadPool;
    private final TPSManager tpsManager;
    private final HealthManager healthManager;
    private final KeepAliveManager keepAliveManager;

    public BatchAgent(String host, int port) {
        super(host, port);
        this.threadPool = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("BatchAgent-%s")
                .build()
        );
        this.tpsManager = new TPSManager();
        this.healthManager = new HealthManager(tpsManager, this);
        this.keepAliveManager = new KeepAliveManager(tpsManager, this);
    }

    public ExecutorService getThreadPool() {
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
        healthManager.start(threadPool);
        keepAliveManager.start(threadPool);

        synchronized (this) {
            wait();
        }
    }

}
