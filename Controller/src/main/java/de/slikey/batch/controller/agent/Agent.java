package de.slikey.batch.controller.agent;

import de.slikey.batch.controller.monitoring.HealthMonitor;
import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.Protocol;
import de.slikey.batch.protocol.PacketHandshake;
import de.slikey.batch.protocol.PacketHealthStatus;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class Agent {

    private static final Logger logger = LogManager.getLogger(Agent.class.getSimpleName());

    private final Channel channel;
    private final HealthMonitor healthMonitor;

    private AgentManager agentManager;
    private AgentState state;

    public Agent(Channel channel) {
        this.channel = channel;
        this.healthMonitor = new HealthMonitor();

        this.state = AgentState.CONNECTING;
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

    public Channel getChannel() {
        return channel;
    }

    public void sendPacket(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void connected() {
        logger.info("Sending handshake to Agent... (" + channel.remoteAddress().toString() + ")");
        channel.writeAndFlush(new PacketHandshake(Protocol.getProtocolHash()));
        state = AgentState.AUTHENTICATE;
    }

    public void disconnected() {

    }

    public void handleHealthStatus(PacketHealthStatus packet) {
        healthMonitor.addHealthStatus(packet);
    }

}
