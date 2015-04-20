package de.slikey.batch.controller;

import de.slikey.batch.controller.agent.AgentManager;
import de.slikey.batch.controller.monitoring.HealthMonitor;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.server.NIOServer;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class BatchController extends NIOServer {

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
    protected PacketChannelInitializer buildPacketChannelInitializer() {
        return new ControllerPacketChannelInitializer(this);
    }

}
