package de.slikey.batch.controller;

import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.packet.Packet2HealthStatus;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerPacketHandler extends PacketHandler {

    private final ControllerConnectionHandler connectionHandler;

    public ControllerPacketHandler(ControllerConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public ControllerConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @Override
    public void handle(Packet2HealthStatus packet) {
        connectionHandler.getAgent().handle(packet);
    }

}
