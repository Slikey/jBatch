package de.slikey.batch.agent;

import de.slikey.batch.agent.job.JobManager;
import de.slikey.batch.agent.misc.KeepAliveManager;
import de.slikey.batch.agent.monitoring.HealthManager;
import de.slikey.batch.network.client.NIOClient;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.protocol.PacketJobStart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class MainClient extends NIOClient {

    private static final Logger logger = LogManager.getLogger(MainClient.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
        new MainClient("localhost", 8080).run();
    }

    private final HealthManager healthManager;
    private final KeepAliveManager keepAliveManager;
    private final JobManager jobManager;

    public MainClient(String host, int port) {
        super(host, port, 32);
        this.healthManager = new HealthManager(this);
        this.keepAliveManager = new KeepAliveManager(this);
        this.jobManager = new JobManager(this);
        this.setReconnect(true);
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }

    public KeepAliveManager getKeepAliveManager() {
        return keepAliveManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    @Override
    protected PacketChannelInitializer buildPacketChannelInitializer() {
        return new MainClientPacketChannelInitializer(this);
    }

    @Override
    protected void started() {
        waitForShutdown();
    }

    @Override
    protected void connected() {
        logger.info("[START] Starting managers...");
        healthManager.start();
        keepAliveManager.start();
        jobManager.start();
        logger.info("[START] Successfully started managers.");
        getExecutorService().schedule(() -> {

            PacketJobStart packet = new PacketJobStart(UUID.randomUUID(), "java -version");
            jobManager.accept(packet);

        }, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void disconnected() {
        logger.info("[STOP] Stopping managers...");
        healthManager.stop();
        keepAliveManager.stop();
        jobManager.stop();
        logger.info("[STOP] Successfully stopped managers.");
    }

    private void waitForShutdown() {
        try {
            synchronized (this) {
                wait();
            }
            close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
