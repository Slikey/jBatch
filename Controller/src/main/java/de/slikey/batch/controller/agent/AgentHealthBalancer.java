package de.slikey.batch.controller.agent;

import java.util.List;
import java.util.Random;

/**
 * @author Kevin Carstens
 * @since 23.04.2015
 */
public class AgentHealthBalancer {

    private final AgentManager agentManager;

    public AgentHealthBalancer(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public Agent getHealthiestAgent() {
        List<Agent> agents = agentManager.getAgents();
        if (agents.size() == 0)
            return null;
        return agents.get(new Random().nextInt(agents.size()));
    }
}
