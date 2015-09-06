package de.slikey.batch.controller;

import de.slikey.batch.controller.agent.Agent;
import de.slikey.batch.controller.agent.AgentManager;
import de.slikey.batch.controller.monitoring.HealthManager;
import de.slikey.batch.network.common.TPSManager;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.server.NIOServer;
import de.slikey.batch.protocol.PacketKeepAlive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class BatchController extends NIOServer {

    private static final Logger logger = LogManager.getLogger(BatchController.class.getSimpleName());

    public static void main(String[] args) {
        new BatchController(8080).run();
    }

    private final ScheduledExecutorService threadPool;
    private final TPSManager tpsManager;
    private final AgentManager agentManager;
    private final HealthManager healthManager;

    public BatchController(int port) {
        super(port);
        this.threadPool = newExecutorService(32);
        this.tpsManager = new TPSManager();
        this.agentManager = new AgentManager(tpsManager, this);
        this.healthManager = new HealthManager(tpsManager, this);
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    public TPSManager getTpsManager() {
        return tpsManager;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }

    @Override
    public void startApplication() throws InterruptedException {
        agentManager.start(threadPool);
        healthManager.start(threadPool);
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                for (Agent agent : agentManager.getAgents()) {
                    agent.sendPacket(new PacketKeepAlive());
                }
                threadPool.schedule(this, 1, TimeUnit.SECONDS);
            }
        });
    }

    @Override
    protected void close() throws InterruptedException {
        super.close();

        threadPool.shutdownNow();
        threadPool.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    protected PacketChannelInitializer buildPacketChannelInitializer() {
        return new ControllerPacketChannelInitializer(this);
    }

}
