package de.slikey.batch.controller.agent;

import de.slikey.batch.controller.BatchController;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class AgentManager {

    private final BatchController batchController;
    private final Map<SocketAddress, Agent> agents;

    public AgentManager(BatchController batchController) {
        this.batchController = batchController;
        this.agents = new HashMap<>();
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

    public Collection<Agent> getAgents() {
        return agents.values();
    }

}
