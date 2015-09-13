package de.slikey.batch.agent.misc;

import de.slikey.batch.agent.MainClient;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.network.protocol.packet.PacketKeepAlive;

/**
 * @author Kevin Carstens
 * @since 28.04.2015
 */
public class KeepAliveManager extends TickingManager {

    private final MainClient mainClient;

    public KeepAliveManager(MainClient mainClient) {
        super(mainClient, 10000);
        this.mainClient = mainClient;
    }

    public MainClient getMainClient() {
        return mainClient;
    }

    @Override
    protected void onTick(double deltaSeconds) {
        mainClient.sendPacket(PacketKeepAlive.instance);
    }

}

