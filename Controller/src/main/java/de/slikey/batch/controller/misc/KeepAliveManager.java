package de.slikey.batch.controller.misc;

import de.slikey.batch.controller.MainController;
import de.slikey.batch.controller.agent.Client;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.network.protocol.packet.PacketKeepAlive;

/**
 * @author Kevin
 * @since 09.09.2015
 */
public class KeepAliveManager extends TickingManager {

    private final MainController mainController;

    public KeepAliveManager(MainController mainController) {
        super(mainController, 10000);
        this.mainController = mainController;
    }

    @Override
    protected void onTick(double deltaSeconds) {
        mainController.getClientManager().getClients().forEach(this::sendKeepAlive);
    }

    public void sendKeepAlive(Client client) {
        client.sendPacket(PacketKeepAlive.instance);
    }

}
