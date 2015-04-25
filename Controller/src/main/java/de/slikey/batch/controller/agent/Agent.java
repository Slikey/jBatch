package de.slikey.batch.controller.agent;

import de.slikey.batch.controller.monitoring.HealthMonitor;
import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.Protocol;
import de.slikey.batch.network.protocol.packet.AgentInformationPacket;
import de.slikey.batch.network.protocol.packet.AuthResponsePacket;
import de.slikey.batch.network.protocol.packet.HandshakePacket;
import de.slikey.batch.network.protocol.packet.HealthStatusPacket;
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
    private AgentInformationPacket information;

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

    public AgentInformationPacket getInformation() {
        return information;
    }

    public Channel getChannel() {
        return channel;
    }

    public void sendPacket(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void connected() {
        logger.info("Sending handshake to Agent... (" + channel.remoteAddress().toString() + ")");
        channel.writeAndFlush(new HandshakePacket(Protocol.getProtocolHash()));
        state = AgentState.AUTHENTICATE;
    }

    public void disconnected() {

    }

    public void handleHealthStatus(HealthStatusPacket packet) {
        healthMonitor.addHealthStatus(packet);
    }

    public void handleAgentInformation(AgentInformationPacket packet) {
        information = packet;
        logger.info("Authenticating Agent (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");

        AuthResponsePacket response = new AuthResponsePacket();
        if (!packet.getName().isEmpty() && packet.getPassword().equals(AgentInformationPacket.PASSWORD)) {
            // Authentication successful
            logger.info("Agent successfully authenticated! (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");
            state = AgentState.WORKING;
            response.setCode(AuthResponsePacket.AuthResponseCode.SUCCESS);
            response.setMessage("OK");
        } else {
            logger.info("cc Agent failed to authenticate! (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");
            state = AgentState.CLOSING;
            response.setCode(AuthResponsePacket.AuthResponseCode.ERROR);
            response.setMessage("ERROR");
        }
        sendPacket(response);

        if (state == AgentState.CLOSING) {
            channel.close();
        }
    }
}
