package de.slikey.batch.controller.agent;

import de.slikey.batch.network.protocol.packet.Packet1Handshake;
import de.slikey.batch.network.protocol.packet.Packet2HealthStatus;
import de.slikey.batch.network.protocol.packet.Packet40AgentInformation;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class Agent {

    private static final Logger logger = LogManager.getLogger(Agent.class);
    private static final int VERSION = 1;

    private final Channel channel;
    private final AgentHealthMonitor healthMonitor;

    private AgentManager agentManager;
    private AgentState state;
    private Packet40AgentInformation information;

    public Agent(Channel channel) {
        this.channel = channel;
        this.healthMonitor = new AgentHealthMonitor();

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

    public Packet40AgentInformation getInformation() {
        return information;
    }

    public Channel getChannel() {
        return channel;
    }

    public void connected() {
        logger.info("cc Sending handshake to Agent (" + channel.remoteAddress().toString() + ")...");
        channel.writeAndFlush(new Packet1Handshake(VERSION));
        state = AgentState.AUTHENTICATE;
    }

    public void disconnected() {

    }

    public void handleHealthStatus(Packet2HealthStatus packet) {
        healthMonitor.addHealthStatus(packet);
    }

    public void handleAgentInformation(Packet40AgentInformation packet) {
        information = packet;
        logger.info("cc Authenticating Agent (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");
        if (packet.getName().equals(Packet40AgentInformation.USERNAME) && packet.getPassword().equals(Packet40AgentInformation.PASSWORD)) {
            // Authentication successful
            logger.info("cc Agent successfully authenticated! (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");
            state = AgentState.WORKING;
        } else {
            logger.info("cc Agent failed to authenticate! (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");
            state = AgentState.CLOSED;
            channel.close();
        }
    }
}
