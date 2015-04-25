package de.slikey.batch.controller.agent;

import de.slikey.batch.controller.BatchController;
import de.slikey.batch.network.protocol.Packet;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.*;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class AgentManager {

    private final BatchController batchController;
    private final Map<SocketAddress, Agent> agents;
    private final AgentHealthBalancer healthBalancer;

    public AgentManager(BatchController batchController) {
        this.batchController = batchController;
        this.agents = new HashMap<>();
        this.healthBalancer = new AgentHealthBalancer(this);
    }

    public void addAgent(Agent agent) {
        agent.setAgentManager(this);
        Agent oldAgent = agents.put(agent.getChannel().remoteAddress(), agent);
        if (oldAgent != null) {
            oldAgent.getChannel().close();
        }
    }

    public void removeAgent(Channel channel) {
        agents.remove(channel.remoteAddress());
    }

    public BatchController getBatchController() {
        return batchController;
    }

    public List<Agent> getAgents() {
        return new ArrayList<>(agents.values());
    }

    public AgentHealthBalancer getHealthBalancer() {
        return healthBalancer;
    }

    public void broadcast(Packet packet) {
        for (Agent agent : agents.values()) {
            agent.sendPacket(packet);
        }
    }

    public void tick() {

    }
}
