package de.slikey.batch.controller.agent;

import de.slikey.batch.network.protocol.packet.Packet2HealthStatus;
import io.netty.channel.Channel;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class Agent {

    private final Channel channel;
    private final String name;
    private final AgentHealthMonitor healthMonitor;

    public Agent(Channel channel, String name) {
        this.channel = channel;
        this.name = name;
        this.healthMonitor = new AgentHealthMonitor();
    }

    public Channel getChannel() {
        return channel;
    }

    public void handle(Packet2HealthStatus packet) {
        healthMonitor.addHealthStatus(packet);
    }

}
