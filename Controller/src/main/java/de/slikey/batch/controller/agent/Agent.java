package de.slikey.batch.controller.agent;

import de.slikey.batch.controller.job.Job;
import de.slikey.batch.controller.monitoring.HealthMonitor;
import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.Protocol;
import de.slikey.batch.protocol.HandshakePacket;
import de.slikey.batch.protocol.PacketAgentInformation;
import de.slikey.batch.protocol.PacketAuthResponse;
import de.slikey.batch.protocol.PacketHealthStatus;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class Agent {

    private static final Logger logger = LogManager.getLogger(Agent.class.getSimpleName());

    private final Channel channel;
    private final HealthMonitor healthMonitor;
    private final List<Job> jobs;

    private AgentManager agentManager;
    private AgentState state;
    private PacketAgentInformation information;

    public Agent(Channel channel) {
        this.channel = channel;
        this.healthMonitor = new HealthMonitor();
        this.jobs = new ArrayList<>();

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

    public PacketAgentInformation getInformation() {
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

    public void handleHealthStatus(PacketHealthStatus packet) {
        healthMonitor.addHealthStatus(packet);
    }

    public void handleAgentInformation(PacketAgentInformation packet) {
        information = packet;
        logger.info("Authenticating Agent (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");

        PacketAuthResponse response = new PacketAuthResponse();
        if (!packet.getName().isEmpty() && packet.getPassword().equals(PacketAgentInformation.PASSWORD)) {
            // Authentication successful
            logger.info("Agent successfully authenticated! (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");
            state = AgentState.WORKING;
            response.setCode(PacketAuthResponse.AuthResponseCode.SUCCESS);
            response.setMessage("OK");
        } else {
            logger.info("cc Agent failed to authenticate! (" + channel.remoteAddress().toString() + " / " + packet.getName() + ")");
            state = AgentState.CLOSING;
            response.setCode(PacketAuthResponse.AuthResponseCode.ERROR);
            response.setMessage("ERROR");
        }
        sendPacket(response);

        if (state == AgentState.CLOSING) {
            channel.close();
        }
    }
}
