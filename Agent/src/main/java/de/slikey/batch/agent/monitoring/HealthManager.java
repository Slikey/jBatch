package de.slikey.batch.agent.monitoring;

import de.slikey.batch.agent.MainClient;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.protocol.PacketHealthStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 28.04.2015
 */
public class HealthManager extends TickingManager {

    private static final Logger logger = LogManager.getLogger(HealthManager.class.getSimpleName());

    private final MainClient mainClient;

    public HealthManager(MainClient mainClient) {
        super(mainClient, 1000);
        this.mainClient = mainClient;
    }

    public MainClient getMainClient() {
        return mainClient;
    }

    @Override
    protected void onTick(double deltaSeconds) {
        if (!mainClient.isRunning()) return;
        mainClient.sendPacket(PacketHealthStatus.create());
    }

    @Override
    protected void onStop() {
    }
}
