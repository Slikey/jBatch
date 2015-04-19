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
        agents.put(agent.getChannel().remoteAddress(), agent);
    }

    public void removeAgent(Channel channel) {
        agents.remove(channel.remoteAddress());
    }

    public Collection<Agent> getAgents() {
        return agents.values();
    }

}
