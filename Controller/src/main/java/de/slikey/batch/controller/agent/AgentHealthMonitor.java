package de.slikey.batch.controller.agent;

import de.slikey.batch.network.protocol.packet.Packet2HealthStatus;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class AgentHealthMonitor {

    private static final int LIMIT_SIZE_1S = 12 * 1000;
    private static final int LIMIT_SIZE_10S = 12 * 1000;

    private final List<Packet2HealthStatus> healthStatuses_1s;
    private final List<Packet2HealthStatus> healthStatuses_10s;
    private long received;

    public AgentHealthMonitor() {
        this.healthStatuses_1s = new LinkedList<>();
        this.healthStatuses_10s = new LinkedList<>();
        this.received = 0;
    }

    public void addHealthStatus(Packet2HealthStatus packet) {
        if (healthStatuses_1s.size() > LIMIT_SIZE_1S)
            healthStatuses_1s.remove(0);
        healthStatuses_1s.add(packet);

        if (received % 10 == 0) {
            if (healthStatuses_10s.size() > LIMIT_SIZE_10S)
                healthStatuses_10s.remove(0);
            healthStatuses_10s.add(packet);
        }

        ++received;
    }

}
