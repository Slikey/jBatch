package de.slikey.batch.agent.monitoring;

import de.slikey.batch.agent.BatchAgent;
import de.slikey.batch.network.common.TPSManager;
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

    private final BatchAgent batchAgent;

    public HealthManager(TPSManager tpsManager, BatchAgent batchAgent) {
        super(tpsManager, 1000);
        this.batchAgent = batchAgent;
    }

    public BatchAgent getBatchAgent() {
        return batchAgent;
    }

    @Override
    protected void onStart() {
        logger.info("Started.");
    }

    @Override
    protected void onTick(double deltaSeconds) {
        batchAgent.sendPacket(PacketHealthStatus.create());
    }

    @Override
    protected void onStop() {
    }
}
