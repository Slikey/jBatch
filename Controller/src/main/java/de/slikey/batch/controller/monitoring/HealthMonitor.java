package de.slikey.batch.controller.monitoring;

import de.slikey.batch.network.protocol.packet.HealthStatusPacket;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class HealthMonitor {

    private static final int LIMIT_SIZE_1S = 3 * 60 * 60;

    private final List<HealthStatusPacket> healthStatuses_1s;

    public HealthMonitor() {
        this.healthStatuses_1s = new LinkedList<>();
    }

    public void addHealthStatus(HealthStatusPacket packet) {
        if (healthStatuses_1s.size() >= LIMIT_SIZE_1S)
            healthStatuses_1s.remove(0);
        healthStatuses_1s.add(packet);
    }

    public List<HealthStatusPacket> getHealthStatuses_1s() {
        return healthStatuses_1s;
    }

}
