package de.slikey.batch.controller;

import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.packet.*;

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
    public void handle(HealthStatusPacket packet) {
        connectionHandler.getAgent().handleHealthStatus(packet);
    }

    @Override
    public void handle(PingPacket packet) {
        connectionHandler.getAgent().sendPacket(PongPacket.create(packet));
    }

    @Override
    public void handle(PongPacket packet) {
        System.out.println(packet);
    }

    @Override
    public void handle(KeepAlivePacket packet) {

    }

    @Override
    public void handle(AgentInformationPacket packet) {
        connectionHandler.getAgent().handleAgentInformation(packet);
    }
}
