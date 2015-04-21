package de.slikey.batch.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.slikey.batch.controller.agent.AgentManager;
import de.slikey.batch.controller.monitoring.HealthMonitor;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.protocol.packet.Packet2HealthStatus;
import de.slikey.batch.network.server.NIOServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class BatchController extends NIOServer {

    private static final Logger logger = LogManager.getLogger(BatchController.class);
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("BatchController")
            .build());

    public static void main(String[] args) {
        new BatchController(8080).run();
    }

    private final AgentManager agentManager;
    private final HealthMonitor healthMonitor;

    public BatchController(int port) {
        super(port);
        this.agentManager = new AgentManager(this);
        this.healthMonitor = new HealthMonitor();
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    @Override
    public void startApplication() {
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("Starting Application: Monitoring Health");
                try {
                    while (true) {
                        healthMonitor.addHealthStatus(Packet2HealthStatus.create());
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected PacketChannelInitializer buildPacketChannelInitializer() {
        return new ControllerPacketChannelInitializer(this);
    }

}
