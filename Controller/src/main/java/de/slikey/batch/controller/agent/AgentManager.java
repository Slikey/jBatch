package de.slikey.batch.controller.agent;

import de.slikey.batch.controller.BatchController;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.network.protocol.Packet;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class AgentManager extends TickingManager {

    private final BatchController batchController;
    private final Map<SocketAddress, Agent> agents;
    private final AgentHealthBalancer healthBalancer;

    public AgentManager(BatchController batchController) {
        super(1000);
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
        Agent agent = agents.remove(channel.remoteAddress());
        if (agent != null)
            batchController.getJobManager().onAgentRemove(agent);
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

    @Override
    protected void onTick(double deltaSeconds) {

    }
}
