package de.slikey.batch.agent;

import de.slikey.batch.network.common.TPSManager;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.protocol.PacketKeepAlive;

/**
 * @author Kevin Carstens
 * @since 28.04.2015
 */
public class KeepAliveManager extends TickingManager {

    private final BatchAgent batchAgent;

    public KeepAliveManager(TPSManager tpsManager, BatchAgent batchAgent) {
        super(tpsManager, 1000);
        this.batchAgent = batchAgent;
    }

    public BatchAgent getBatchAgent() {
        return batchAgent;
    }

    @Override
    protected void onTick(double deltaSeconds) {
        if (!batchAgent.isRunning()) return;
        batchAgent.sendPacket(new PacketKeepAlive());
    }

}

