package de.slikey.batch.controller.agent;

import de.slikey.batch.network.protocol.packet.Packet2HealthStatus;
import io.netty.channel.Channel;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class Agent {

    private final Channel channel;
    private final AgentHealthMonitor healthMonitor;

    private AgentManager agentManager;
    private AgentState state;
    private String name;

    public Agent(Channel channel, String name) {
        this.channel = channel;
        this.healthMonitor = new AgentHealthMonitor();

        this.state = AgentState.AUTHENTICATE;
        this.name = name;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public AgentState getState() {
        return state;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Channel getChannel() {
        return channel;
    }

    public void connected() {

    }

    public void disconnected() {

    }

    public void handle(Packet2HealthStatus packet) {
        healthMonitor.addHealthStatus(packet);
    }

}
